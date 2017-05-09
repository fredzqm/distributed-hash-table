# url_connect [![Build Status](https://travis-ci.org/zhoutwo/url_connect.svg?branch=master)](https://travis-ci.org/zhoutwo/url_connect)
A Google Chrome extension that enables users to talk to others who are viewing the same web page

## Development Setup
Install the latest `yarn` package manager.
```
  npm install -g yarn
  yarn install
```
This will use the `yarn.lock` file increase the speed of installs.

## Build
To run watched builds:
```
  yarn start
```
This will build a `popup.js` script in `.\client\src\browser_action\popup.js`. Open Chrome and load the `client` at `chrome://extensions/`. After every build, you must reload the extension.

## Linter
To run the linter:
```
  yarn lint
```
 The linter settings are defined in `.eslintrc.json`.
