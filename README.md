# MetriX
MetriX is just an experiment of multiple technologies and tools which execute one simple task - help to free some space at your Dropbox account. It may look like killing flies with rocket launcher but actually here we have that case when the goal is less important than a path to it. 

Working instance of MetriX is available by following URL: https://metrix.redcraft.net 
###Description
So let's see what we have here...

- **Backend**: Java + Framworks([Guice] , [Jersey])
- **UI**: [AngularJS], [UI Bootstrup] ([Twitter Bootstrap] port for AngularJS), [LESS], [tc-angular-chartjs] ([Charts.js] port for AngularJS)
- **Build**: [Maven], [Grunt]
- **Deployment**: optimised for [Heroku] deployment

What you may find helpful in this project (problems solved):

* Support authentication on UI using AngularJS
* Global HTTP errors handling using AngularJS
* Minification of JavaScript, CSS and AngularJS HTML Templates
* Combining Grunt and Maven build processes
* Making Heroku support Grunt+Maven build process

So, if you have any questions related to these topics - feel free to ask.

###Build and Run How-To

I assume that you have Grunt and maven installed. You will need the following environment variables for build and run:
* API_KEY - Dropbox API key;
* API_SECRET - Dropbox API secret;
* FIXED_THREAD_POOL_SIZE - number of threads for parsing Dropbox accounts. Recommended number: 3.

If you are running on Heroku also add the following variables:

* BUILDPACK_URL - we need to chose special buildpack which will run both Grunt and Maven. Set variable to: https://github.com/whyjustin/heroku-buildpack-grunt-maven.git;
* HTTPS_ONLY - this is hack for Heroku. Just set it to "true";
* JAVA_TOOL_OPTIONS - set value to "-Dfile.encoding=UTF-8" otherwise you'll have problems with locales;
* PRODUCTION_BUILD - this variable can be used for switching on/off JS/CSS/Templates minification.

If you are building on your own machine, run the following command to build artifact
```sh
$ mvn clean && npm install && grunt && mvn package
```
or just run command below to start service in jetty
```sh
$ mvn clean && npm install && grunt && mvn jetty:run
```

That's it. Thanks for your interest :-)

[Twitter Bootstrap]:http://twitter.github.com/bootstrap/
[AngularJS]:http://angularjs.org
[Gulp]:http://gulpjs.com
[Guice]:https://github.com/google/guice
[Jersey]:https://jersey.java.net
[UI Bootstrup]:http://angular-ui.github.io/bootstrap/
[LESS]:http://lesscss.org
[tc-angular-chartjs]:https://github.com/carlcraig/tc-angular-chartjs
[Charts.js]:http://www.chartjs.org
[Heroku]:https://www.heroku.com
[Grunt]:http://gruntjs.com
[Maven]:http://maven.apache.org

