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
íf sudo ls | grep task6
then
  echo "dir exists"
else
  sudo mkdir /task6
fi
sudo cp -rvf * /task6
''')
  }
}