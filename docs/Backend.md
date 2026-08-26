# Assumptions
* Since the application is for a Demo, it is acceptable to make it work properly for a single user, thus no sessions or personalized questions lists.
* Murphy's law has a 100% bonus chance of making a surprise appearance during a Demo.
* The more I know, the more I understand how little I know.

# The creation
The idea I had was simple load questions in batch, to dodge the TooManyRequests exceptions I ran into while creating the frontend PoC, store them and expose them while stripping the correct answer value using a DTO.

I chose TDD so I would always be confident to change the implementation which helped me a lot down the road when I ran into the nightmare of in-memory lists that I did not see before should have used SQLite (joking).

I prefered using for-loops over streams since for loops are more efficient. I only used streams when a stream would improve readability.

I logged exceptions and warnings only if it is worth alerting, since logging everything can become a money sink over time.


# Other choices
* Use records over classes since the data should be immutable, no complex business logic required except the stripping of the correct answer before exposing the data.
* Called OpenDB with base64 encoding, so I dont get weird html characters and can parse it before storing the data.
* Fetched highest amount during OpenDB loading so the load on OpenDB is minimal.
* Chose in memory storing over using a database as it is more lightweight and data persistence is not required.

## The Trivia storage (sounds simple right)
Instead of a database I used an in-memory list. This saves spinning up a database but comes with its own caviats 🐹.  I went with a simple list at first, but it was quite clear this was not going to cut it. I then used ```AtomicReference``` which was great, but too complex and not a right fit. Eventually I ended up with ```ConcurrentHashMap```.

## Database versus In-memory
In hindsight a database was probably the quicker solution. It would have handled concurrency issues and race conditions and with an ORM it would be plug and play (keeping the n+1 queries in mind though).

I chose to store the questions in memory because a database for a demo felt overkill. I opted to make the service functions easy to change by using a central ```ConcurrentHashMap``` that could be easily replaced with a repository that does CRUD operations on a database.

Secondly if I want to host this in the cloud, as I intended, I dont want to spend time setting up a (free) database and forget about it after the demo, thus wasting server resources (even though I assume they optimize this, [right?](https://www.reddit.com/media?url=https%3A%2F%2Fi.redd.it%2Frcdal3nps8p91.jpg).



If I had to pick a database for this demo, I would have used SQLite. Since it is an in-memory representation of the solution I picked now. And it would be easy to host with the backend.

Lastly, I like the challenge, why do things the same way over and over if I can learn a lesson by making it harder for myself.

## The journey

A database handles the transaction (if you use locks). Each change is added to a queue and executed one by one. Like a queue at a ticket booth. It always ensures that the ticket vendor has an accurate stack of available tickets. Each customer needs to be told that a ticket is available, waits until the vendor has scanned the ticket and receives it.

The in memory list handles it differently out of the box. Customers sprint to the booth, they get told that a ticket is available. If the ticket has not been passed to the customer, another one can come up and ask if there is a ticket available. Since the scanning-in-progress ticket has not been handed over, the customer is told that there is a ticket available.

To solve this I made the list and the content immutable. This mean there are no side effects. Another measure was wrapping the in memory list in an `AtomicReference`. This allows modification to be wrapped in transactions, just like a database would under the hood.

For a single player game this would be enough. For a multiplayer game, unfortunately, another caviat 🐹 should be solved in the future. 

To keep the list from growing infinitely, when a question is answered it is deleted. When the list is empty, a new bulk fetch is done to the TriviaAPI. This means when two users receive the same question and Henk evaluates his answer before Anna. Anna's will evaluate on a non-existing question. I added a `RuntimeException` so this is handled gracefully. 

To make multiplayer robust, sessions should be implemented and each user should get their own list of questions in memory. 

## The problem Anna points to

I intended to leave it a this. Happy with having parked an issue out of scope.

Yet I could not let go of Anna's problem, the actual problem: Data corruption. What if Henk and Anna try to press submit at the same exact millisecond? The first night I thought: "Why solve something that is never going to happen within the scope of this application?". Waving Anna's problem away. The second night however sleep would not come, Anna started bothering me again: "You will have to open pandora's box some day. You can run but you cant hide.". So I sighed, got out of bed, grabbed my screwdriver and opened the box.

What happens when Henk and Anna answer questions for hours to end up exactly at the same question and then count down together to press "Evaluate Answer" at the exact same time?

Two different actions are ran; 1. get the record from the list in the atomicreference and 2. delete the question from the list in the ```AtomicReference```. A race condition could occur even though it is wrapped in an ```AtomicReference```, they both check then they do a modification to the list. Even though the list is wrapped in a thread-safe wrapper the modifications to it are not.

The main issue is that after Henk and Anna both fetch the question by its ID, they both are eager to remove the question. If Henk does this before Anna does, Anna will receive a RuntimeException instead of an evaluation.

## Solving the race condition

The solution I ran into (which also removed again a lot of code) was ```ConcurrentHashmap```. I can set an ID as the key, this is already a UUID so it is (highly probable) unique. I tried question, but sometimes i would still use this Question ID as the lookup which again could cause race conditions. The key, value pair is atomic, which means thread safe. The list of values (```TriviaQuestion``` objects) however is not.

It comes with great stuff that let me remove my code such as ```putIfAbsent``` or ```remove``` which both use the key to manipulate the pairs inside it. Thus these methods can ensure they hide all the transactional stuff that I had to write code for when using ```AtomicReference```. 
 
Anna still gets her exception, at least it is a "true" exception because her request came in later than Henk's.

## Making Anna happy

By making all get, update and remove operations on the in memory storage thread-safe Anna's problem is almost solved.

The next step is not returning questions that have already been presented to a user. For this a boolean is added to the record. This flag is used as a filter only adding it to the list that is returned to Anna or Henk when ```isPresented``` is False. Before adding a question to the output list, the flag is set using a thread-safe operation on the hashmap.

The replace function checks if the old value that is passed as the second argument is equal to the current value only then it will replace it with the new value passed as the third argument. When it does a replacement it will return True, otherwise False.

So by doing a replacement and only adding the question to the output list if this replacement returns True, Anna's problem is finally solved. Multiplayer has been achieved without sessions.

In the end this could also have been solved with ```AtomicReference``` and a few ```synchronized```, yet ```ConcurrentHashMap``` is a proven abstraction that other developers already have had sleepless nights over. And it saves me a lot of lines of code. And locking a single value is better than wrapping a whole list. And it is aesthetically pleasing. And instead of overengineering on a database, I overengineered on a 1/10000000 chance.

Maybe an SQLite database was not a bad idea at all.

## Further reading
I ran into [this stackoverflow](https://stackoverflow.com/questions/51951124/concurrenthashmapstring-ref-vs-atomicreferencemapstring-ref) that did a pretty good explanation about tradeoffs and different concurrency implementations (although it is in Kotlin, "🥔" or "🥔"). A rare gem of actual usefull stackoverflow content.

# Future Considerations

## Adding retry mechanism
The external trivia API can return 429 too many requests. This wont happen if only a single user uses this API. It would be good to add a retry mechanism in the future when this code is returned from the external API.

## Multi tenant
Because one instance hold its own list scaling vertically would be a challenge. Especially with a round-robin. This would require a single source of truth for all tenants like a database or a cache like redis.

If we go back to Henk and Anna. When Henk gets a question from instance A and then checks his answer with instance B the question with its unique UUID would not be present on instance B, thus this will result in a ```QuestionNotFoundException```. SQLite would not cut it. For now I will keep this box closed.

## Race condition on fetch
There is still a race condition possible during the getting of questions, during this the service checks if the question pool is almost empty (size < 5) if Henk and Anna fetch at the same time they could trigger two fetches at the same time. This could be solved with an ```AtomicBoolean``` that is set to True during before starting a fetch and set to False after. Letting Anna wait while Henk is fetching questions. 

This chance however, is quite small and the network-latency with two users make this chance even slimmer. The effect might be that the external API returns a 429, yet this is already handled. If both request go through the result would be that instead of 50, 100 questions get added. This has no impact on the user experience (since both would be waiting even with a boolean that triggers a while or sleep).

Since it would add complexity that does not add much value, I pass for now.