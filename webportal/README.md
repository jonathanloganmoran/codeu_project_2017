## To Use, Visit: <a href="http://codeu.dgarry.com" target="_blank">codeu.dgarry.com</a>

## To Run Locally:
Install composer, a package manager for PHP.

```
brew update
brew install homebrew/php/composer
```

Then **navigate to the root of the webportal folder** and install dependencies.

`composer install`

Run the server.

`composer start`

Navigate to **localhost:8080** in your browser.

## PHP versioning
We use PHP7.
```
brew update
brew tap homebrew/dupes
brew tap homebrew/versions
brew tap homebrew/homebrew-php

# unlink previous php70 if you have it installed
brew unlink php70
brew install php71

# check if php is now version 7.1.0
php --version

# should result in terminal output similar to:
PHP 7.1.2 (cli) (built: MONTH DAY YEAR HOURS:MINUTES:SECONDS) ( NTS )
Copyright (c) 1997-2017 The PHP Group
```
