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
if sudo ls | grep /root/task6
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
    echo "WebApp already running"
fi
if sudo ls /root/task6/ | grep *.html
then
    echo "Launching WebApp"
    sudo kubectl apply -f htdeploy.txt
    pod=$(sudo kubectl get pods -l app=myweb -o jsonpath="{.items[0].metadata.name}")
    sleep 30
    sudo kubectl cp /root/task6/*.html $pod:/usr/local/apache2/htdocs
elif sudo ls /root/task6/ | grep *.php
then
    echo "Launching WebApp"
    sudo kubectl apply -f phdeploy.txt
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
status=$(curl -o /dev/null -s -w %{http_code} 192.168.99.100:31180/) #put your ip
if [ $status == 200 ]
then
    echo "OK"
    exit 0
else
    echo "Error"
    sudo curl --user admin:goloo005 "http://192.168.99.101:8085/job/job4-mail-dev/build?token=sendmail"
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
buildPipelineView('DevOpsTask6') {
    filterBuildQueue()
    filterExecutors()
    title('CI/CD pipeline using Seed Job')
    displayedBuilds(3)
    selectedJob('job1-pull-repo')
    alwaysAllowManualTrigger()
    showPipelineParameters()
    refreshFrequency(15)
}
