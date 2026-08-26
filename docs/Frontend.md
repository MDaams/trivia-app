# Assumptions
* Since the application is for a Demo, it is acceptable to make it work properly for a single user, thus no sessions or personalized questions lists.
* Murphy's law has a 100% bonus chance of making a surprise appearance during a Demo.
* The more I know, the more I understand how little I know.

# Choices
* Used React since I have experience with that, next.js would be too heavy I would only recommend it if SEO is important. For a demo trivia app, SEO is irrelevant.
* Used vite since it is a more modern ```create-react-app```.
* Used the standard tester with jsdom, since I have experience with jsdom.
* Created one component for the ```QuestionForm``` with atomized components, so it is easy to test what is shown.
* Wrapped the ```QuestionForm``` with an ```App``` component. So the fetching and state management is separated from the rendering components.
* Did not add e2e testing with playwright or cypress, since the app is small and straightforward for now, manual testing suffices.
* Tried to use as little dependencies as possible, this prevents dependency hell in the future.

# PoC
Started out with a PoC UI calling the openDB API directly so I could test the limits of the API. Then build the backend and replaced the openDB with this proxy. Because I already had built the frontend I could simply plug in the new proxy by replacing the url. I then started to migrate the logic (decoding HTML, parsing booleans to human readable language, etc..) to the proxy.