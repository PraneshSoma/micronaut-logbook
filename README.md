# micronaut-logbook


This repo has the custom log book with the fix for the below.

Issue : 

runEagerly method in Sequence class in netty package is triggered only for the first requests. All subsequent requests are not getting into the loop since the next is incremented in the last line of the class. As the server and client handlers set the sequence at 0, 1. Next should get reset for every http request. 

