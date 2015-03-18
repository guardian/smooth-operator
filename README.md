# Overview

This is a repo for experimenting with an app which will handle our
on-call support phone number routing.

The idea that we're kicking around so far, is that the app will
integrate with both [Twilio](https://www.twilio.com/docs/api) and
[PagerDuty](http://www.pagerduty.com/) to provide a seamless way to
contact the person who is currently on call. Specifically, by
providing an HTTP end-point that will get pinged when we receive a
call, and which will then look up who is currently on the rota via
PagerDuty, and telling Twilio where to forward the call.

Currently only the first part of call forwarding is implemented, so it
will provide an end-point that will forward the caller to a static
number configured in the conf file.

# Gory-details

This is a simple [Play](https://playframework.com/) application that
talks [TwiML](https://www.twilio.com/docs/api/twiml), which as the
name suggests is an XML format that contains commands telling Twilio
what you want it to do when it receives a call.

It is built using the official Twilio
[Java library](https://www.twilio.com/docs/java/install).

To use it, just run it by doing something like `sbt run` and then you
need to configure Twilio to ping the URL that your app is listening
on. As this URL needs to be accessible to the internet, a good tool
for testing it with is (ngrok)[http://ngrok.com/].
