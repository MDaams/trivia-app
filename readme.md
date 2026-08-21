# Assumptions
* Since the application is for a Demo, it is acceptable to make it work properly for a single user.

# Concurrency and Race Conditions
Instead of a database I used an in-memory list. This saves spinning up a database but comes with its own caviats. 

A database handles the transaction. Each change is added to a queue and executed one by one. Like a queue at a ticket booth. It always ensures that the ticket vendor has an accurate stack of available tickets. Each customer needs to be told that a ticket is available, waits until the vendor has scanned the ticket and receives it.

The in memory list handles it differently out of the box. Customers sprint to the booth, they get told that a ticket is available. If the ticket has not been passed to the customer, another one can come up and ask if there is a ticket available. Since the scanning-in-progress ticket has not been handed over, the customer is told that there is a ticket available.

To solve this I made the list and the content immutable. This mean there are no side effects. Another measure was wrapping the in memory list in an `AtomicReference`. This allows modification to be wrapped in transactions, just like a database would under the hood.

For a single player game this would be enough. For a multiplayer game, unfortunately, another caviat should be solved in the future. 

To keep the list from growing infinitely, when a question is answered it is deleted. When the list is empty, a new bulk fetch is done to the TriviaAPI. This means when two users receive the same question and Henk evaluates his answer before Anna. Anna's will evaluate on a non-existing question. I added a `RuntimeException` so this is handled gracefully. 

To make multiplayer robust, sessions should be implemented and each user should get their own list of questions in memory. 


# Future Considerations

The external trivia API can return 429 too many requests. This wont happen if only a single user uses this API. It would be good to add a retry mechanism in the future when this code is returned from the external API.