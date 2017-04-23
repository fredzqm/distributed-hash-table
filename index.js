let http = require('http'),
    httpProxy = require('http-proxy'),
    scheduler = require('./scheduler');
    url = require('url');

let proxy = httpProxy.createProxyServer({});

let server = http.createServer(function(req, res) {
  if (req.method === 'GET' && req.url === '/register') {
    url.parse(req.url, true);
    scheduler.registerWorker(host);
  } else {
    proxy.web(req, res, { target: scheduler.getNextWorker()});
  }
});
 
server.listen(1338);

