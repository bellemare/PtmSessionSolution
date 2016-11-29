Q: "As a bonus, consider what additional data would help make better analytical conclusions"
The payTM userID would be the best way to attribute events to users. This could be done by sending the ID along with the GET and PUT requests, provided there exists a secure way to do this. The client would be responsible for sending this information along to the web server.


Q: For this dataset, complete the sessionization by time window rather than navigation. Feel free to determine the best session window time on your own, or start with 15 minutes.
 I chose a 30 minute timeout for the session window length, as this is how Google Analytics does their sessionalization:
 https:support.google.com/analytics/answer/2731565?hl=en

 The principle here is that a user will have all their events within a single session, provided the ordered list does not have
 any gaps greater than 30 minutes. Once a gap larger than 30 minutes exists, a new session will begin. As Paytm may have its own specialized session requirements, this number can be changed as necessary.


Some issues that I ran into:
1) A single-event session. A user that has a single request, such as a GET, could be shown as having a session length of 0 seconds.
A minimum session length of a small but reasonable number, such as 5-10 seconds for a viewing the contents of a page, could be used to illustrate a user reading a page.
 On the other hand, this could also be misleading by boosting the average session length and skewing the distribution of session length results. Further work needs to be done to investigate the nature of single-session events.

2) Is the session length strictly the delta from Event 0 to Event N? Or Should there be padding around it?
Are we more interested in the user's experience on the client side, or are we only concerned about server-side activity?

