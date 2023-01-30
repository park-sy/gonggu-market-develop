const { client } = require("../tools/jwt");
const { redisCli, get, set } = require("../tools/cache");
const db = require("../tools/db");

const login = async (req, res) => {
  //const { username, password } = req.body;

  const nickname = req.body.userID;
  const password = req.body.userPWD;
  const [result] = await db.query(
    `SELECT * FROM User WHERE nickname = '${nickname}'`
  );

  const user = result[0];

  if (user) {
    const chk = password === user.password;
    if (chk) {
      const accessToken = client.sign(user);
      //const refreshToken = client.refresh();
      //console.log(`refreshToken : ${refreshToken}`);
      //await set(username, refreshToken);
      console.log("✅ POST LOGIN RETRUN", user);
      console.log(user);
      return res.status(200).json({
        ok: true,
        data: {
          //accessToken: accessToken,
          //refreshToken,
          userID: user.email,
          userNick: user.nickname,
        },
      });
    } else {
      return res.status(401).send({
        ok: false,
        message: "password is incorrect",
      });
    }
  } else {
    console.log("❌ Post Login - Fail");
    return res.status(401).send({
      ok: false,
      message: "user does not exist",
    });
  }
};

module.exports = { login };
