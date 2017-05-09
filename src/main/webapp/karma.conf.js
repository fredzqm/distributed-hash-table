const webpackConfig = require('./webpack.config');

module.exports = function(config) {
  config.set({
    basePath: "component",
    frameworks: ["mocha", "sinon-chai", "chai"],
    browsers: ["Chrome"],
    plugins: [
      "karma-chrome-launcher",
      "karma-webpack",
      "karma-mocha",
      "karma-chai",
      "karma-sinon-chai",
      "karma-chai-as-promised",
      "karma-spec-reporter"
    ],
    files: [
      "**/*.spec.tsx"
    ],
    preprocessors: {
      // add webpack as preprocessor
      "**/*.spec.tsx": ["webpack"]
    },
    webpack: {
      module: webpackConfig.module,
      resolve: webpackConfig.resolve
    },
    reporters: [
      "spec"
    ],
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: true,
    webpackMiddleware: {
      // webpack-dev-middleware configuration
      // i. e.
      // stats: "errors-only"
    }
  });
};
