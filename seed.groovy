freeStyleJob('job1-pull-repo') {
  description("pull codes from GitHub")
  scm {
    github('https://github.com/sarthakSharma5/myRepo.git', 'master')
  }
  triggers {
    scm("* * * * *")
  }
  steps {
    shell('''
íf sudo ls | grep task6
then
echo "directory exists"
else
sudo mkdir /task6
sudo cp -rvf * /task6
''')
  }
}