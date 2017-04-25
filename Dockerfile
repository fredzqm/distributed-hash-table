FROM node:boron
RUN mkdir -p /app
ADD . /app
WORKDIR /app
RUN npm install
EXPOSE 1337
CMD [ "npm", "start"]

