# devops_task6
For creating a CI/CD pipeline using Groovy Language as DSL for Jenkins.
Refer the article on the link given below:
https://www.linkedin.com/pulse/creating-cicd-pipeline-using-groovy-sarthak-sharma

Step-wise description of the project:
Step-1: Use a 'Seed Job' to pull the groovy script available on this repository uploaded by Dev.
Step-2: Use Job DSL plugin in Jenkins to generate Jobs and Pipeline according to the script.
Step-3: Require approval of Jenkins Admin to build the Seed Job.

Description of Script 'seed.groovy':
The Script will be used to create 4 jobs and a BUild-pipeline View in Jenkins
Jobs: job1-pull-repo, job2-deploy-code, job3-check-status, job4-mail-dev
Pipeline: DevOps Task6 with title('CI/CD pipeline using Seed Job')

Pipeline description:
1. Pull code for webpage from the GitHub repository as soon as Dev push new codes
2. Check code and use approriate manifest file to launch over Kubernetes Cluster as deployments
3. Check the 'status code' of the webpage and mail the Dev/Admin in case of some error
GitHub repository used: https://github.com/sarthakSharma5/repo_web
To mail Dev/Admin a Python Code is used, refer README.md of the repository given above

A Jenkins portal is launched ontop of Docker-CE as a container
Dockerfile for the same is available on the same repository given in 'Pipeline Description'

The Jobs are created/updated using freeStyleJOb('job-name')
1. for Build trigger: use scm(), upstream() under triggers; authenticationToken('token-name')
2. specify Source Code Management: use github() under scm
3. to execute commands in shell: use shell() under steps
4. to describe jobs: use description()

Pipeline is created using buildPipelineView('view-name')
1. view title: give using title()
2. set the numbber of builds to display: displayedBuilds()
3. specify initial job: selectedJob('job-name')
4. allow manual trigger of pipeline: alwaysAllowManualTrigger()
5. set the refresh rate of view: refreshFrequency()

To understand the script better or for reference visit https://jenkinsci.github.io/job-dsl-plugin/
