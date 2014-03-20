---
layout: post
title: "Up and running on AppEngine with Maven, Jersey and Guice - Part 5"
date: 2014-03-18 18:23:59 +0000
comments: true
categories: [AppEngine, Java, JPA]
---
This is part five of my post on getting a web application up and running on Google AppEngine with Maven, Jersey and Guice.

[Part 4](/blog/2014/03/18/up-and-running-on-appengine-with-maven-jersey-and-guice-part-4/) was around getting Jersey set up with JSON and Freemarker. This part works through getting Persistence working with JPA.  
<!-- more -->
##Working with the Datastore##
The final part of this tutorial is getting data saved and retrieved from the AppEngine Datastore. There are a number of options for tools to help you do this including the Java Persistence API (JPA), Java Data Objects (JDO), Objectify or the Datastore's Low-level API. For reasons of familiarity I've chosen to proceed with JPA. There are many discussions out there as to the best option, but the short summary is that it really is dependent on your application and application needs. This was again a fairly complex task to get right, and a big thank you to the author of [this piece](http://www.loop81.com/2013/02/gae-google-app-engine-jpa2-maven-and.html) which put me in the right direction. 

To get started with JPA the first thing we need is a big bunch of dependencies. 
``` xml
<dependency>
  <groupId>org.datanucleus</groupId>
  <artifactId>datanucleus-api-jpa</artifactId>
  <version>${datanucleus.jpa.version}</version>
</dependency>

<dependency>
  <groupId>org.datanucleus</groupId>
  <artifactId>datanucleus-core</artifactId>
  <version>${datanucleus.jpa.version}</version>
</dependency>

<dependency>
  <groupId>com.google.appengine.orm</groupId>
  <artifactId>datanucleus-appengine</artifactId>
  <version>2.1.1</version>

  <exclusions>
    <exclusion>
      <groupId>org.datanucleus</groupId>
      <artifactId>datanucleus-enhancer</artifactId>
    </exclusion>
  </exclusions>
</dependency>

<dependency>
  <groupId>javax.jdo</groupId>
  <artifactId>jdo-api</artifactId>
  <version>3.0.1</version>
</dependency>

<dependency>
  <groupId>org.apache.geronimo.specs</groupId>
  <artifactId>geronimo-jpa_2.0_spec</artifactId>
  <version>1.1</version>
</dependency>

<dependency>
	<groupId>org.hibernate</groupId>
	<artifactId>hibernate-validator</artifactId>
	<version>5.1.0.Final</version>
</dependency>
```

The one thing to note is note is that from the AppEngine ORM package, we're excluding the built-in datanucleus enhancer. This is what takes your `@Entity` annotated classes and adds to them to make them databaseable. Instead we're going to use the `maven-datanucleus-plugin` to do the enhancement. 
``` xml
<plugin>
  <groupId>org.datanucleus</groupId>
  <artifactId>maven-datanucleus-plugin</artifactId>
  <version>${datanucleus.jpa.version}</version>
  
  <configuration>
    <api>JPA</api>
    <mappingIncludes>**/entities/*.class</mappingIncludes>
    <verbose>true</verbose>
  </configuration>
  
  <dependencies>
    <dependency>
      <groupId>org.datanucleus</groupId>
      <artifactId>datanucleus-core</artifactId>
      <version>${datanucleus.jpa.version}</version>
    </dependency>
  </dependencies>
  
  <executions>
    <execution>   
      <phase>compile</phase>
      <goals>
        <goal>enhance</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

If you're working with Eclipse and the m2eclipse plugin, this will work from the command line, but not eclipse. To proceed in Eclipse, configure the m2e plugin to allow execution of the datanucleus plugin.
``` xml
<pluginManagement>
  <plugins>
    <plugin>
      <groupId>org.eclipse.m2e</groupId>
      <artifactId>lifecycle-mapping</artifactId>
      <version>1.0.0</version>
      
      <configuration>
        <lifecycleMappingMetadata>
          <pluginExecutions>
            <pluginExecution>
              <pluginExecutionFilter>
                <groupId>org.datanucleus</groupId>
                <artifactId>maven-datanucleus-plugin</artifactId>
                <versionRange>${datanucleus.jpa.version}</versionRange>
              
                <goals>
                  <goal>enhance</goal>
                </goals>
              </pluginExecutionFilter>

              <action>
                <execute >
                  <runOnIncremental>false</runOnIncremental>
                </execute >
              </action>
            </pluginExecution>
          </pluginExecutions>
        </lifecycleMappingMetadata>
      </configuration>
    </plugin>
  </plugins>
</pluginManagement>
```

Now if you run the `compile` goal, you should see log messages indicating that DataNucleus has run. Some of these may appear a little scary, since the logging levels from DataNucleus leave a little to be desired, but look for the message `DataNucleus Enhancer completed with success` and you should be good.

Now, let's actually give DataNucleus something to do. Here's my incredibly simple test entity. 

{% include_code 5/SimpleTestEntity.java %}

Let's create a new couple of methods in the Resource to exercise this entity - one to create it, and one to retrieve it from the datastore. 
``` java
@GET
@Produces(MediaType.TEXT_HTML)
@Path("/persist")
@Template(name="/foo.ftl")
public Map<String, Object> persist(@QueryParam("name") String name, @QueryParam("message") String message) {
	Map<String, Object> map = new HashMap<>();
	map.put("foo", name);
	map.put("bar", message);
	SimpleTestEntity ste = new SimpleTestEntity();
	ste.setName(name);
	
    EntityManagerFactory factory = Persistence.createEntityManagerFactory("transactions-optional");
    EntityManager entityManager = factory.createEntityManager();
    entityManager.getTransaction().begin();
    entityManager.persist(ste);
    entityManager.getTransaction().commit();

	return map;
}

@GET
@Produces(MediaType.TEXT_HTML)
@Path("/retrieve")
@Template(name="/foo.ftl")
public Map<String, Object> retrieve() {
	Map<String, Object> map = new HashMap<>();
	
    EntityManagerFactory factory = Persistence.createEntityManagerFactory("transactions-optional");
    EntityManager entityManager = factory.createEntityManager();
    entityManager.getTransaction().begin();
    Query q = entityManager.createQuery("select t from " + SimpleTestEntity.class.getSimpleName() + " t");
    List<?> list = q.getResultList();
    map.put("foo", "Number of Entries");
    map.put("bar", "The number of database entries is: " + list.size());
    
    entityManager.getTransaction().commit();

	return map;
}
```

Before everything will work nicely, we also need to create a `persistence.xml` file. This file goes in your `src/main/resources` directory, in a subdirectory named `META-INF`. 
{% include_code 5/persistence.xml %}

Now if we run the `/Hey/persist` method a couple of times, we should be adding entities to our datastore:

{% img /images/postimages/5/persist.png %}	

And to be sure, load the `/Hey/retrieve` URL every few times and make sure you get the expected number:

{% img /images/postimages/5/retrieveNumber.png %}

Deploy to appengine using `mvn appengine:update` and make sure that everything works the same with the real datastore.

That should all be good. Now lets use the built in datastore viewer to make sure that our objects look right. For the real datastore you can visit `appengine.google.com` and click "Datastore Viewer" from the left hand navigation bar. For the devserver, you should be able to visit `http://localhost:8080/_ah/admin/` to do the same thing. Unfortunately, if you try it now you'll get a 404 when you try to access it. The reason? Jersey thinks it should know how to respond to that URL, and it doesn't. This is only a problem on the devserver, but it's something that we should fix anyway. Let's tell Jersey that it isn't responsible for URLs that start with `/_ah`.

First we create our own Filter, that subclasses the Jersey filter (unfortunately named `ServletContainer` because it can be used in multiple ways):

{% include_code 5/JerseyFilter.java %}

Then in our `web.xml` we use our new class as the `filter-class` instead of `ServletContainer`.

``` xml
<filter-class>com.listerly.config.jersey.JerseyFilter</filter-class>
```

Now load up the two datastore viewers and everything should look as you wanted.

{% img /images/postimages/5/datastoreViewerLocal.png %}	

{% img /images/postimages/5/datastoreViewerAppengine.png %}	

##Making  JPA easy##
Part 5B of this guide was going to be around making JPA easier to use - showing how to create abstract DAOs and concrete implementations for your persistent objects; showing how to make the EntityManager injectable; showing how you could use a filter to start and commit the transaction. However after trying a couple of different approaches out, it became clear that everyone is going to want something slightly different here. That my approach is pretty much guaranteed not to be your approach. So instead, I've decided to stop the tutorial here.

##Final words##
The final source code for this tutorial is available from github here https://github.com/akshayrangnekar/gaemavenjerseyguice - please drop me a line on twitter (@akshayr) if you run into any problems, or find something that I've done wrong. 
