## To Install
Install composer, a package manager for PHP.

```
brew update
brew install homebrew/php/composer
```

Then navigate to the root of this project and install dependencies.

`composer install`

Run the server.

`composer start`

Navigate to localhost:8080.
## Testing
```
composer install
brew update
brew install selenium-server-standalone
brew install chromedriver
```

in one tab of terminal, type in `selenium-server -port 4444` to run the selenium browser process

in another tab, navigate to lyricloud folder and enter `vendor/bin/behat features/` to run the tests
##### Useful Documentation
- [Behat Quick Start](http://behat.org/en/latest/quick_start.html)
- [Behat Mink Guide](http://docs.behat.org/en/v2.5/cookbook/behat_and_mink.html)

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
