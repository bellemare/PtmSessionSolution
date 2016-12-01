How to build:
1) git clone the project
2) cd to project root
3) execute "sbt assembly". This should build the fat jar with the dependencies.

How to run:
1) bin/run_with_defaults.sh <time_in_seconds>
Note: The application will run in local mode, unless the spark-submit parameters are modified to specify a cluster.

Results:
View the results of the various session windows in the results folder.


Questions:
Q: "As a bonus, consider what additional data would help make better analytical conclusions"
The payTM userID would be the best way to attribute events to users. This could be done by sending the ID along with the GET and PUT requests, provided there exists a secure way to do this. The client would be responsible for sending this information along to the web server.


Q: For this dataset, complete the sessionization by time window rather than navigation. Feel free to determine the best session window time on your own, or start with 15 minutes.
 I first chose a 30-minute timeout for the session window length, as this is how Google Analytics does their sessionalization:
 https:support.google.com/analytics/answer/2731565?hl=en

 The principle here is that a user will have all their events within a single session, provided the ordered list does not have any gaps greater than 30 minutes. Once a gap larger than 30 minutes exists, a new session will begin. Further investigation into the events in the sessions and how they correlate to the user's experience could be conducted to determine some guidelines for session length.  Either way, this number can be changed as necessary, or even a strict maximum window-size could be applied with few changes to the code.


Some issues that I ran into:
1) A single-event session. A user that has a single request, such as a GET, could be shown as having a session length of 0 seconds.
A minimum session length of a small but reasonable number, such as 5-10 seconds for a viewing the contents of a page, could be used to illustrate a user reading a page.
 On the other hand, this could also be misleading by boosting the average session length and skewing the distribution of session length results. Further work needs to be done to investigate the nature of single-session events.

2) Is the session length strictly the delta from Event 0 to Event N? Or Should there be padding around it?
Are we more interested in the user's experience on the client side, or are we only concerned about server-side activity?

3) Regex for parsing logs. There could be some corner cases that I have not covered. In addition, the pattern matching speed could be an issue. I do use a greedy match in there to get around some issues I had with matching the REST requests.
Given that we have a very good idea of what the logs are supposed to look like, there is a chance for performance gains here.

4) Not entirely sure what constitutes a unique web page visit. I simply looked to match the entire string, but there may be additional logic baked into the URL that I need to address or parse. Either way, the code is modular enough that only a single function will need to be modified to fix that.


