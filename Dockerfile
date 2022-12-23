FROM --platform=linux/amd64 node:18

ENV ACCESS_KEY_ID=AKIAXO2JL7XQAQFQIS4A  \
    SECRET_ACCESS_KEY=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 \
    REGION=ap-northeast-2

WORKDIR /usr/src/app

# Install app dependencies
COPY package*.json ./
RUN npm install

# Bundle app source
COPY . .

CMD [ "npm", "start" ]