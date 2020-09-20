freeStyleJob('job1-pull-repo') {
  description("pull codes from GitHub")
  scm {
    github('sarthakSharma5/repo_web', 'master')
  }
  triggers {
    scm("* * * * *")
  }
  steps {
    shell(''' echo "copying code to workspace"
if sudo ls /root/ | grep task6
then
  echo "dir exists"
else
  sudo mkdir /root/task6
fi
sudo cp -rvf * /root/task6/
''')
  }
}
freeStyleJob('job2-deploy-code') {
  description('launch app over Kubernetes Cluster')
  triggers {
    upstream('job1-pull-repo', 'SUCCESS')
  }
  steps {
    shell ('''
if sudo kubectl get deploy | grep myweb
then
    echo "Clearing Environment"
    kubectl delete all --all
fi
if sudo ls /root/task6/ | grep .html
then
    echo "Launching WebApp"
    sudo kubectl apply -f htdeploy.yaml
    pod=$(sudo kubectl get pods -l app=myweb -o jsonpath="{.items[0].metadata.name}")
    sleep 30
    sudo kubectl cp /root/task6/*.html $pod:/usr/local/apache2/htdocs
elif sudo ls /root/task6/ | grep .php
then
    echo "Launching WebApp"
    sudo kubectl apply -f phdeploy.yaml
    pod=$(sudo kubectl get pods -l app=myweb -o jsonpath="{.items[0].metadata.name}")
    sleep 30
    sudo kubectl cp /root/task6/*.php $pod:/var/www/html
else
    echo "appropriate file not found"
fi
''')
  }
}
freeStyleJob('job3-check-status') {
  description('check status of the WebApp')
  triggers {
    upstream('job2-deploy-code', 'SUCCESS')
  }
  steps {
    shell('''
status=$(curl -o /dev/null -s -w %{http_code} <K8S_CLUSTER>:31180/) #put IP of Minikube
if [ $status == 200 ]
then
    echo "OK"
    exit 0
else
    echo "Error"
    sudo curl --user admin:<JENKINS_ADMIN_PASSWORD> "http://<SYSTEM_IP>:8085/job/job4-mail-dev/build?token=sendmail"  # Use Jenkins Admin Password
    exit 1
fi
''')
  }
}
freeStyleJob('job4-mail-dev') {
  description('mail developer if error in code')
  authenticationToken('sendmail')
  steps {
    shell('''
# execute bash commands
sudo python3 /root/task6/mail.py
''')
  }
}
buildPipelineView('DevOps Task6') {
    filterBuildQueue()
    filterExecutors()
    title('CI/CD pipeline using Seed Job')
    displayedBuilds(2)
    selectedJob('job1-pull-repo')
    alwaysAllowManualTrigger()
    showPipelineParameters()
    refreshFrequency(10)
}
