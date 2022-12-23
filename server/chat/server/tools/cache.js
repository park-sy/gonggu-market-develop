const redis = require("redis");
const redisClient = redis.createClient({ legacyMode: true });
redisClient.connect().then();
const redisCli = redisClient.v4;

const set = async (key, value) => {
  await redisCli.set(key, value);
};

const get = (req, res, next) => {
  let key = req.originalUrl;
  redisCli.get(key, (error, data) => {
    if (error) {
      res.status(400).send({
        ok: false,
        message: error,
      });
    }
    if (data !== null) {
      console.log("data from redis!");
      res.status(200).send({
        ok: true,
        data: JSON.parse(data),
      });
    } else next();
  });
};

module.exports = {
  redisCli,
  set,
  get,
};
