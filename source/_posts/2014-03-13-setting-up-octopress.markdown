---
layout: post
title: "Setting up Octopress"
date: 2014-03-13 13:22:41 +0000
comments: true
categories: Octopress
---
{% img left http://www.gravatar.com/avatar/712764174a1641db425b971456e63015?r=x&s=700 150 150 'me' 'me' %} I've been looking for a nice system to use to create blog posts as I get back in to development. I've seen a couple of very ncie blogs that use [Octopress](http://octopress.org/), so I decided to give it a try.

I did the setup on a my fairly new MacBook Pro, running OS X Mavericks. Was fairly easy, even with my incredibly rusty knowledge of Ruby but there were a couple of gotchas that took a little bit of [Stack Overflowing](http://stackoverflow.com/) to figure out so I thought it may be worth writing this up as my first post using the system. With these notes, should take you less than five minutes to install and be up and running.
<!-- more -->
## Installation
Installation was *almost* as easy as the documentation suggested. 

* I already had git installed from XCode. If you don't, it's fairly simple to install (just type git for instructions).

* The version of ruby that ships with OSX is good enough to get Octopress up and running. 

* Getting Octopress was as simple as cloning a repo
``` console 
git clone git://github.com/imathis/octopress.git octopress
cd octopress
```

* Here's where it starts getting a little more complicated. 

Following the instructions, next step was to download prerequisites:
``` console
mbp:octopress$ bundle install
Fetching gem metadata from https://rubygems.org/.......
Fetching additional metadata from https://rubygems.org/..

Errno::EACCES: Permission denied - /Library/Ruby/Gems/2.0.0/build_info/rake-0.9.2.2.info
An error occurred while installing rake (0.9.2.2), and Bundler cannot continue.
Make sure that `gem install rake -v '0.9.2.2'` succeeds before bundling.
```
Uh oh. But this one is fairly simple to figure out - the OS X Gems system installs ruby gems into a superuser writable directory by default. Easy fix.
``` console
mbp:octopress$ sudo gem install bundler
Password:
Fetching: bundler-1.5.3.gem (100%)
Successfully installed bundler-1.5.3
Parsing documentation for bundler-1.5.3
Installing ri documentation for bundler-1.5.3
1 gem installed
```
Well that was easy. 

* Now we have to install the bundle
``` console
mbp:octopress$ bundle install
Fetching gem metadata from https://rubygems.org/.......
Fetching additional metadata from https://rubygems.org/..

Errno::EACCES: Permission denied - /Library/Ruby/Gems/2.0.0/build_info/rake-0.9.2.2.info
An error occurred while installing rake (0.9.2.2), and Bundler cannot continue.
Make sure that `gem install rake -v '0.9.2.2'` succeeds before bundling.
mbp:octopress$ sudo gem install rake -v '0.9.2.2'
rake's executable "rake" conflicts with /usr/bin/rake
Overwrite the executable? [yN]  N
ERROR:  Error installing rake:
	"rake" from rake conflicts with /usr/bin/rake
mbp:octopress$ rake -version
rake aborted!
undefined local variable or method `rsion' for #<Rake::Application:0x007fc41c8782b8>

(See full trace by running task with --trace)
```
Shit. The sudo workaround isn't going to cut it. And don't really want to keep doing sudo to get all this working. Better solution: fix gem install to happen locally. 

* Set the gem install directory (for good measure, also do this in .profile)
``` console
mbp:octopress$ export GEM_HOME=/Users/akshay/.gem/ruby/2.0.0/
mbp:octopress$ echo $GEM_HOME
/Users/akshay/.gem/ruby/2.0.0/
```

* And try again
``` console
mbp:octopress$ bundle install
Fetching gem metadata from https://rubygems.org/.......
Fetching additional metadata from https://rubygems.org/..
Installing rake (0.9.2.2)

Gem::Installer::ExtensionBuildError: ERROR: Failed to build gem native extension.

    /System/Library/Frameworks/Ruby.framework/Versions/2.0/usr/bin/ruby extconf.rb 
checking for main() in -lc... yes
creating Makefile

make "DESTDIR="
compiling redcloth_attributes.c
compiling redcloth_inline.c
compiling redcloth_scan.c
linking shared-object redcloth_scan.bundle
clang: error: unknown argument: '-multiply_definedsuppress' [-Wunused-command-line-argument-hard-error-in-future]
clang: note: this will be a hard error (cannot be downgraded to a warning) in the future
make: *** [redcloth_scan.bundle] Error 1


Gem files will remain installed in /Users/akshay/.gem/ruby/2.0.0/gems/RedCloth-4.2.9 for inspection.
Results logged to /Users/akshay/.gem/ruby/2.0.0/gems/RedCloth-4.2.9/ext/redcloth_scan/gem_make.out
An error occurred while installing RedCloth (4.2.9), and Bundler cannot
continue.
Make sure that `gem install RedCloth -v '4.2.9'` succeeds before bundling.
```

* Shit. Now this looks like it's going to take all day (it's not).

Fortunately, some helpful soul had already answered this on [StackOverflow](http://stackoverflow.com/questions/22352838/ruby-gem-install-json-fails-on-mavericks-and-xcode-5-1-unknown-argument-mul). Again, a fairly simple solution. (Again, another line for my .profile)
``` console
mbp:octopress$ export ARCHFLAGS=-Wno-error=unused-command-line-argument-hard-error-in-future gem install json
mbp:octopress$ gem install RedCloth -v '4.2.9'

mbp:octopress$ gem install RedCloth -v '4.2.9'
Building native extensions.  This could take a while...
Successfully installed RedCloth-4.2.9
Parsing documentation for RedCloth-4.2.9
unable to convert "\xCF" from ASCII-8BIT to UTF-8 for ext/redcloth_scan/redcloth_attributes.o, skipping
unable to convert "\xCF" from ASCII-8BIT to UTF-8 for ext/redcloth_scan/redcloth_inline.o, skipping
unable to convert "\xCF" from ASCII-8BIT to UTF-8 for ext/redcloth_scan/redcloth_scan.bundle, skipping
unable to convert "\xCF" from ASCII-8BIT to UTF-8 for ext/redcloth_scan/redcloth_scan.o, skipping
unable to convert "\xCF" from ASCII-8BIT to UTF-8 for lib/redcloth_scan.bundle, skipping
Installing ri documentation for RedCloth-4.2.9
1 gem installed
```

* Well there are some warnings, but they're in the documentation and overall it looks better. Lets try the main dependencies installation again.
``` console
mbp:octopress$ bundle install
Fetching gem metadata from https://rubygems.org/.......
Fetching additional metadata from https://rubygems.org/..
Using rake (0.9.2.2)
Using RedCloth (4.2.9)
Installing chunky_png (1.2.5)
Installing fast-stemmer (1.0.1)
Installing classifier (1.3.3)
Installing fssm (0.2.9)
Installing sass (3.2.9)
Installing compass (0.12.2)
Installing directory_watcher (1.4.1)
Installing haml (3.1.7)
Installing kramdown (0.13.8)
Installing liquid (2.3.0)
Installing syntax (1.0.0)
Installing maruku (0.6.1)
Installing posix-spawn (0.3.6)
Installing yajl-ruby (1.1.0)
Installing pygments.rb (0.3.4)
Installing jekyll (0.12.0)
Installing rack (1.5.2)
Installing rack-protection (1.5.0)
Installing rb-fsevent (0.9.1)
Installing rdiscount (2.0.7.3)
Installing rubypants (0.2.0)
Installing sass-globbing (1.0.0)
Installing tilt (1.3.7)
Installing sinatra (1.4.2)
Installing stringex (1.4.0)
Using bundler (1.5.3)
Your bundle is complete!
Use `bundle show [gemname]` to see where a bundled gem is installed.
```

* Looks good. Now to complete the install
``` console
mbp:octopress$ rake install
## Copying classic theme into ./source and ./sass
mkdir -p source
cp -r .themes/classic/source/. source
mkdir -p sass
cp -r .themes/classic/sass/. sass
mkdir -p source/_posts
mkdir -p public
``` 
Awesome. That's the basic install done.

## Configuration
I decided that the best thing for me was to do use this blog as my page for github. There are a bunch of [other deployment options](http://octopress.org/docs/deploying/) if you don't want to do that.

First step is to create a new repo in github. It must be named `<your username>.github.io` (or more accurately, `<your username>/<your username>.github.io`). Screenshot below shows how mine was setup.

{% img /images/postimages/github-repo-create.png %}	
 
Then tell Octopress how to deploy to your github page.
``` console
mbp:octopress$ rake setup_github_pages
Enter the read/write url for your repository
(For example, 'git@github.com:your_username/your_username.github.io.git)
           or 'https://github.com/your_username/your_username.github.io')
Repository url: https://github.com/akshayrangnekar/akshayrangnekar.github.io.git
Added remote https://github.com/akshayrangnekar/akshayrangnekar.github.io.git as origin
Set origin as default remote
Master branch renamed to 'source' for committing your blog source files
rm -rf _deploy
mkdir _deploy
cd _deploy
Initialized empty Git repository in /Users/akshay/Dev/Octopress/octopress/_deploy/.git/
[master (root-commit) e8d4b07] Octopress init
 1 file changed, 1 insertion(+)
 create mode 100644 index.html
cd -

---
## Now you can deploy to https://github.com/akshayrangnekar/akshayrangnekar.github.io.git with `rake deploy` ##
```

We're getting close to done. Set some configuration options in the `_config.yml` file:
```
# ----------------------- #
#      Main Configs       #
# ----------------------- #

url: http://akshayrangnekar.github.io
title: Akshay's Programming Blog
subtitle: Notes on Coding
author: Akshay Rangnekar
simple_search: https://www.google.com/search
description:
```
and further down
```
# ----------------------- #
#   3rd Party Settings    #
# ----------------------- #

# Github repositories
github_user: akshayrangnekar
github_repo_count: 1
github_show_profile_link: true
github_skip_forks: true

# Twitter
twitter_user: akshayr
twitter_tweet_button: true

# Google +1
google_plus_one: false
google_plus_one_size: medium

# Google Plus Profile
# Hidden: No visible button, just add author information to search results
googleplus_user: akshay.rangnekar@gmail.com
googleplus_hidden: true
```

And we're ready to deploy.
```
mbp:octopress$ rake generate
## Generating Site with Jekyll
directory source/stylesheets/ 
   create source/stylesheets/screen.css 
Configuration from /Users/akshay/Dev/Octopress/octopress/_config.yml
Building site: source -> public
Successfully generated site: source -> public
mbp:octopress$ rake deploy
## Deploying branch to Github Pages 
## Pulling any updates from Github Pages 
cd _deploy
There is no tracking information for the current branch.
Please specify which branch you want to merge with.
See git-pull(1) for details

    git pull <remote> <branch>

If you wish to set tracking information for this branch you can do so with:

    git branch --set-upstream-to=origin/<branch> master

cd -
rm -rf _deploy/index.html

## Copying public to _deploy
cp -r public/. _deploy
cd _deploy

## Committing: Site updated at 2014-03-13 12:04:21 UTC
[master a87f356] Site updated at 2014-03-13 12:04:21 UTC
 61 files changed, 1118 insertions(+), 1 deletion(-)
 <LINES REMOVED>
## Pushing generated _deploy website
Counting objects: 79, done.
Delta compression using up to 8 threads.
Compressing objects: 100% (72/72), done.
Writing objects: 100% (79/79), 186.51 KiB | 0 bytes/s, done.
Total 79 (delta 1), reused 0 (delta 0)
To https://github.com/akshayrangnekar/akshayrangnekar.github.io.git
 * [new branch]      master -> master

## Github Pages deploy complete
cd -
```

And we're done (almost). Just make sure you commit your code to the repo. 
```
mbp:octopress$ git status
On branch source
Your branch is based on 'origin/master', but the upstream is gone.
  (use "git branch --unset-upstream" to fixup)

Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
  (use "git checkout -- <file>..." to discard changes in working directory)

	modified:   Rakefile
	modified:   _config.yml

Untracked files:
  (use "git add <file>..." to include in what will be committed)

	sass/
	source/

no changes added to commit (use "git add" and/or "git commit -a")
mbp:octopress$ git add .
mbp:octopress$ git commit -m 'First commit'
[source 13f9d4a] First commit
 119 files changed, 2784 insertions(+), 3 deletions(-)
 <LINES REMOVED>
mbp:octopress$ git push origin source
Counting objects: 4771, done.
Delta compression using up to 8 threads.
Compressing objects: 100% (2124/2124), done.
Writing objects: 100% (4771/4771), 1.30 MiB | 136.00 KiB/s, done.
Total 4771 (delta 2296), reused 4742 (delta 2270)
To https://github.com/akshayrangnekar/akshayrangnekar.github.io.git
 * [new branch]      source -> source
mbp:octopress$ 
```

Now we're done. And ready to create our first post. 
```
mbp:octopress$ rake new_post["Setting up Octopress"]
mkdir -p source/_posts
Creating new post: source/_posts/2014-03-13-setting-up-octopress.markdown
```
And then start editing the file at `source/_posts/2014-03-13-setting-up-octopress.markdown`. If you need help on the syntax, the [Daring Fireball page](http://daringfireball.net/projects/markdown/syntax) is a great place to go. 
