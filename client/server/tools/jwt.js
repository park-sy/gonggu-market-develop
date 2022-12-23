const { promisify } = require("util");
const jwt = require("jsonwebtoken");
const redisClient = require("./cache");
//const db = require("../tools/authdb");

const jwtSecret = process.env.JWT_SECRET;

const client = {
  sign: (user) => {
    //access token 발급
    const payload = {
      //user custom
      id: user.ID,
      nick: user.NICK,
    };

    return jwt.sign(payload, process.env.JWT_SECRET, {
      algorithm: "HS256",
      expiresIn: "1h",
    });
  },
  verify: (token) => {
    //access token 검증
    let decoded = null;
    try {
      decoded = jwt.verify(token, jwtSecret);
      return {
        //user custom
        ok: true,
        id: decoded.id,
        nick: decoded.nick,
      };
    } catch (err) {
      return {
        ok: false,
        message: err.message,
      };
    }
  },
  refresh: () => {
    //refresh token 발급
    return jwt.sign({}, process.env.JWT_SECRET, {
      algorithm: "HS256",
      expiresIn: "14d",
    });
  },
  refreshVerify: async (token, userId) => {
    //refresh token 검증 ??
    const getAsync = promisify(redisClient.get).bind(redisClient); //??
    try {
      const data = await getAsync(userId);
      if (token === data) {
        try {
          jwt.verify(token, jwtSecret);
          return true;
        } catch (err) {
          return false;
        }
      } else {
        return false;
      }
    } catch (err) {
      return false;
    }
  },
};

module.exports = { client };
