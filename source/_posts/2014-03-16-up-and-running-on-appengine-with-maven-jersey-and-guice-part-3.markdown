---
layout: post
title: "Up and running on AppEngine with Maven, Jersey and Guice - Part 3"
date: 2014-03-18 12:23:37 +0000
comments: true
categories: [AppEngine, Java, Jersey, Maven, Guice]
---
This is part three of my post on getting a web application up and running on Google AppEngine with Maven, Jersey and Guice.

[Part 2](/blog/2014/03/14/up-and-running-on-appengine-with-maven-jersey-and-guice-part-2/) got us to the point of having a working web application running locally and deployed on AppEngine. Part 3 will get Guice & Jersey up and running. 

<!-- more -->
##Guice up##
After the relative nightmare of some of the other things we've done in earlier parts, Guice is remarkably easy to get working. Some of it is due to good documentation, but mostly it's just a great framework that's very easy to use.

The first step is to include the appropriate dependencies in our `pom.xml`. Here's the modified pom.xml that includes Guice and the Guice-Servlet packages. Here's the reworked file. See lines 24-33 for changes:

{% include_code 3/pom.xml %}

Next, we setup our Guice modules. First, we extend `ServletModule` to help serve our HelloWorldServlet:

{% include_code 3/ListerlyServletModule.java %}

Then we extend `GuiceServletContextListener` to tell Guice about our new `ServletModule`:

{% include_code 3/ListerlyGuiceServletContextListener.java %}

Finally, we have to load Guice and our ServletContentListener in the web.xml. Notice we now take out our Servlet definition.

{% include_code 3/web.xml %}

Make a quick change to HelloWorldServlet to prove that you're actually hitting the new Servlet (I've now got `Hello, handrolled, guiced AppEngine!` as my message). Then run the devserver and hit the server. You should be looking good:

{% img /images/postimages/3/guiced.png %}	

Great, Guice is clearly serving our Servlet, but let's make absolutely sure by injecting a class. I've created a class called `TestClass`. I'm marking it as a `@Singleton` to ensure that Guice only creates it once:

{% include_code 3/TestClass.java %}

Then in my HelloWorldServlet I inject the `TestClass` object, and print out the date it is loaded with:

{% include_code 3/HelloWorldServlet.java %}

Reload the page a few times. Things are behaving as you would expect: you get the time at which you first loaded the page. Hopefully Jersey will be that easy!

##Jersey? Sure.##
There are a bunch of really [good](http://blog.palominolabs.com/2011/08/15/a-simple-java-web-stack-with-guice-jetty-jersey-and-jackson/) [articles](https://sites.google.com/a/athaydes.com/renato-athaydes//posts/jersey_guice_rest_api) on how to use Jersey with Guice. Unfortunately they're all based on Jersey 1.X and Jersey 2.0 is now out. Jersey 2.0 seems to be a different story judging by [this](https://java.net/jira/browse/HK2-39), [this](https://java.net/jira/browse/HK2-121), [this](https://java.net/jira/browse/HK2-39) and [this](https://hk2.java.net/guice-bridge/index.html). 

It seems like Jersey's inclusion of the HK2 dependency injection framework means lots of problems with getting Guice up and running. There are three options to proceed: (1) Revert back to better understood Jersey 1.X library, (2) Switch from Guice to HK2, or (3) Try to get something going with the help of a couple of [working](https://github.com/aluedeke/jersey2-guice-example) [samples](https://github.com/piersy/jersey2-guice-example-with-test).
 
I decided to push through and try #3. I'm a big fan of Guice and don't think HK2 is as mature so option 2 was out. Option 1 is tempting, but seemed like the fallback option since I figure at some stage Jersey 1.X is going to be too outdated to continue with so better to eat the pain now rather than after I have 100s of services. 
 
The following instructions get things working. I'm not sure they're the perfect (or best) way to do things, but after a day of struggling I'm going to settle for what I have. Things do at least seem to be working. 
 
First, lets start with the dependencies. Here's my final working `pom.xml`:
 
{% include_code 3/b/pom.xml %}

Notice the new dependencies for Jersey, Freemarker (my template language of choice with Jersey) and the HK2-Guice bridge. 

Next we have to change our `web.xml` to use Jersey:

{% include_code 3/b/web.xml %}

This now has a filter defined for Jersey (lines 19-30), which is configured with (a) our Application (more on that in a second) and (b) the Freemarker mumbo-jumbo. The filter, like the Guice filter, is applied to all URLs.

The next file to go through is the "Application" for Jersey, which defines how Jersey is set up:

{% include_code 3/b/JerseyConfiguration.java %}

Three important parts to note here: (1) Line 24 tells Jersey which package will contain our Resources - the POJOs that will respond to requests. (2) Line 26 tells Jersey we will be using Freemarker. (3) Lines 28-30 set up the HK2-Guice bridge. 

Next, lets create a simple POJO to respond to requests:

{% include_code 3/b/HeyResource.java %}

Lets fire up the devserver and load `http://localhost:8080/Hey`

{% img /images/postimages/3/hey.png %}	

That all looks good. But let's make absolutely sure injection is working correctly. 

Lets add a second injectable class to begin. Notice that this one is RequestScoped, as opposed to our previous one which was a Singleton:

{% include_code 3/c/SecondTest.java %}

We can now add a second method and inject a couple of objects into our class: 

{% include_code 3/c/HeyResource.java %}

Run the server, and everything looks good:

{% img /images/postimages/3/heyFoo.png %}

Push everything to AppServer and make sure everything is fine. We'll be using more of Jersey and Guice in the next couple of steps as we get started on templates and persistence. [Part 4](/blog/2014/03/18/up-and-running-on-appengine-with-maven-jersey-and-guice-part-4/) will build on today, getting Jersey set up with JSON and Freemarker.