const { client } = require("../tools/jwt.js");

const authJWT = (req, res, next) => {
  if (req.headers.authorization) {
    const token = req.headers.authorization.split("Bearer ")[1];
    const result = client.verify(token);
    if (result.ok) {
      //req.id = result.id;
      //req.role = result.role;
      //next(); //////없앨까???
      console.log(`ID : ${result.id} Nick : ${result.nick}`);
      res.json({
        message: "SUCCESS!!",
      });
    } else {
      return res.status(401).send({
        ok: false,
        message: result.message,
      });
    }
  }
};

module.exports = { authJWT };
