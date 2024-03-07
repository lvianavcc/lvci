# CI sandbox

A reasonably good test bed for CI workflows based on Jenkins and Gerrit:

- Bring the system up and down from scratch and test out new configurations
- Test interactions between different serviced, e.g. whenever a tag is
  created on a Gerrit repository, a pipeline is run on Jenkins.

Such workflows are common place. Think about it. You want to do a release. The
best way I know to release software is to mark a specific commit on the `master`
branch (a.k.a. `dec`, `trunk`, `main`) as the released content. That can be done
with a tag and/or a branch.

So we can simply push a new tag to a Git repository and Jenkins will "hear" that
and start the entire CI machinery to perform that release: be it running further
testing, copying binaries to a distribution location, updating the documentation
website, and so on.

Another common workflow is to perform tests on any commits pushed to a branch
that starts with `feature/` and upload a "test score" tag to the Git server. On
Gerrit that is the "Verified" vote, but GitHub, GitLab and others all have their
mechanisms.

This project does not intend to develop these mechanisms themselves, though. It
intends to provide an easy starting point for _you_ to develop your own.

## Getting started

Start by [installing Docker Compose] if you haven't yet.

### Gerrit setup

We first need to setup the Gerrit container by running the `init` command.

- Open the `compose.yaml` file and uncomment the `command:` line in the Gerrit service,
- run: `docker compose up gerrit`

The container starts, runs the `init` command, and stops.

- comment out the `command:` line
- start the `gerrit` service again with `docker compose up gerrit`

Now Gerrit should be online.

- navigate to http://localhost:8081
- login as `admin` and setup an admin SSH key
- logout of the `admin` account and login as `jenkins`
- setup the `jenkins` SSH key. Prefer to use the one from the `ssh` directory
  because it is pre-configured to work with the Jenkins server

### Jenkins setup

- bring up the Jenkins server and the Docker-in-Docker runner provider: with
  `docker compose up`
- access the Jenkins server at `localhost:8080`.

Note: The Gerrit Trigger plugin for Jenkins is somewhat picky for accepting
SSH keys. When configuring this plugin we need to provide an SSH key, and this
key __must__ be created as `-m PEM` using the default RSA algorithm. If not,
the plugin simply does not accept the key, and Jenkins gets no access to the
Gerrit event stream.

## How Jenkins is configured

The Jenkins server is based on the [`jenkins/jenkins`] Docker image. This image is
further customized in the `jenkins-lv` Dockerfile.

The instance is configured with the [JCasC] plugin (configuration as code). The
configuration file lives under `jenkins-config/jenkins.yaml`.

Further reading:

- https://www.digitalocean.com/community/tutorials/how-to-automate-jenkins-job-configuration-using-job-dsl

## How Gerrit is configured

The Gerrit service is based on the `gerritcodereview/gerrit` Docker image. There
is a Gerrit configuration file under `gerrit-config/gerrit.config` which gets
mounted in the correct place in the Gerrit container.

## Create a project

The reason we are doing all this is to be able to perform automated tasks when
things happen in our project. So we need a project. In the Gerrit UI, browse to
`Browse > Repositories` and click CREATE NEW.

## Create a pipeline

1. Create a repository named `pipelines`, where your pipelines will live. This
   repository name is currently hard-coded in `jenkins.yaml`.
2. Clone the `pipelines` repository locally.
3. Create a `myjob.groovy` file, commit and push it. Alternatively use the
   `sample-job.groovy` as a starting point.
4. Run the seed job on Jenkins
5. Tadaaa! Your new pipeline appears. On the Jenkins interface.


## Bringing the system down

To start and stop the containers without removing them:

    docker compose stop

And to stop the containers and remove them:

    docker compose down

If you want to throw away all the Jenkins data (pipelines, etc.), and the Gerrit
data, you should also delete the volumes with:

    docker compose down -v



[installing Docker Compose]: https://docs.docker.com/compose/install/
[`jenkins/jenkins`]
