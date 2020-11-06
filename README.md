# micronaut-logbook


This repo is to demonstrate the issue with custom log book 

Issue : 

Sequence(Linked List) member variable in LogbookServerHandler and LogbookClientHandler in package 'org.zalando.logbook.netty'is causing 
runEagerly method in Sequence class is triggered only for the first requests. 

All subsequent requests are not getting into the loop since the next is incremented in the last line of the class. As the server and client handlers set the sequence at 0, 1. Next should get reset for every http request. 

This only happens for keep-alive connections.

please use the below postman collection to replicate the issue.

https://www.getpostman.com/collections/002b75f4175a43980ef8


Potential Solve:
Sequence Member variable has to be instatiated inside the first function (i.e. ChannelRead in `LogbookServerHandler` and ChannelWrite in `LogbookClientHandler`).
I will raise a PR for this issue and update here.
