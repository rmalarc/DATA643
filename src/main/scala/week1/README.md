# Week 1 Discussion - Scenario Design

Your task for the first discussion is to analyze an existing recommender system that you find interesting.  You should:

1. Perform a Scenario Design analysis as described below.  Consider whether it makes sense for your selected recommender system to perform scenario design twice, once for the organization (e.g. Amazon.com) and once for the organization's customers.
2. Attempt to reverse engineer what you can about the site, from the site interface and any available information that you can find on the Internet or elsewhere.
3. Include specific recommendations about how to improve the site's recommendation capabilities going forward.

Create your report using either an R Markdown file or an Jupyter (IPython) notebook, and create a discussion thread with a link GitHub repo where your Markdown file or Jupyter notebook resides.  You are not expected to need to write code for this assignment.


# Response

Let's consider for a minute the LinkedIn weekly email that advertises matching job postings:

![Sample Email from LinkedIn](https://raw.githubusercontent.com/rmalarc/DATA643/master/src/main/scala/week1/linkedin_Page_1.jpg)

## 1. Scenario Design Analysis

1. Who are your target users?: This recommender systems serves the company that has the job offering primarily. Although it is arguable that the recipient of the email is a secondary target user in this recsys, I would consider the LinkedIn member a user whenever he or she is actively searching for a job or performing other operations in the main website.
2. What are their goals?: The goal of the posting company is to fill the vacancy with qualified candidates.
3. How can you help them accomplish those goals? LinkedIn can get clicks to the job posting by advertising the vacancy to qualified individuals in the LinkedIn Network.

## 2. Dissecting the Recomender System

This is a good example of a recommender system that serves a non-realtime workflow. This fits the mold of the many recommender systems that specialize in email offers.

In this particular, the system can be content management based. The system may be driven by the following datapoints:

* From the LinkedIn Member Profile:
  * Skills
  * Employment History Description
  * Location
* From the Job posting:
  * Job description

## 3. Recommendations

I have always gotten this email in a regular basis. This leads me to believe that the current system does not take into consideration if I'm actually looking for a job and what kind of a job I'm looking for.

In order to capture "intent to quit", I would try to find a proxy amongst:

* Job searches
* Content of directed messages to other members
* Directed messages to recruiters
* Accepted connections from recruiters

