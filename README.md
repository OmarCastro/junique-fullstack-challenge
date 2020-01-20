# Junique challenge

### Steps

1. Setup development environment
    1. Install SBT with play for backend development.
    2. Install NPM with webpack and react for frontend development.
    3. Integrate Webpack with sbt to run npm task on SBT.
 
2. Develop frontend application base
    1. Create a skeleton of the application views
    2. Create react components with mocked state
 
3. Develop backend application base
    1. Develop HomeController
    2. Define Model for Page information
    3. Develop service to parse a document from url using JSoup and transform it to Page information
 
4. Integrate backend with frontend
    1. Create play javascript routes
    2. Create util to make request using js routes
    3. Use data from backend
    4. Remove frontend mock data
    
5. Test the application
    1. Create ComponentSpec to test use cases of getInfoFromPage method of WebPageService

### Assumptions/Decisions

 * Only successful html pages are processed, in other words, error pages, like 404 not found are not processed, 
 as they do not hold much value.

 * Restricted login form verification to 2 inputs: a username input and a password input, there are many ways to define
 a login form, be it with, divs, form element, custom elements, or even span's and use css to make it look like a form.
 The existence of those 2 field is a good enough hint of a login form.
 
 * If no protocol is used on the form, it prepends the "http://" as it is the most common protocol used to get
 html pages.
 
 * When clicking processing the data, the submit button is disabled as to prevent multiple requests at the same time.
 Saving server resources to do another jobs, also minimizing the possibility of a successful DDoS attack using the
 browser. 
  



