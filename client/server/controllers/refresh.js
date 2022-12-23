const { sign, verify, refreshVerify } = require("../tools/jwt");
const jwt = require("jsonwebtoken");

const refresh = async (req, res) => {
  // check access token and refresh token exist
  if (req.headers.authorization && req.headers.refresh) {
    const authToken = req.headers.authorization.split("Bearer ")[1];
    const refreshToken = req.headers.refresh;

    // access token verify
    const authResult = verify(authToken);

    // access token decoding
    const decoded = jwt.decode(authToken);

    if (decoded === null) {
      return res.status(401).send({
        ok: false,
        message: "Not authorized!",
      });
    }

    // refreshToken verify
    let user = null;
    try {
      user = await client.users.findFirst({
        where: {
          id: decoded.id,
        },
      });
    } catch (err) {
      return res.status(401).send({
        ok: false,
        message: err.message,
      });
    }

    const refreshResult = refreshVerify(refreshToken, user.username);

    if (authResult.ok === false && authResult.message === "jwt expired") {
      // 1. accessToken expired && refreshToken expired
      if (refreshResult.ok === false) {
        return res.status(401).send({
          ok: false,
          message: "Not authorized!",
        });
      } else {
        // 2. accessToken expired && refreshToken valid => make new accessToken
        const newAccessToken = sign(user);
        return res.status(200).send({
          ok: true,
          data: {
            accessToken: newAccessToken,
            refreshToken,
          },
        });
      }
    } else {
      // authResult == TRUE
      // 3. accessToken valid => don't have to make new token
      return res.status(400).send({
        ok: false,
        message: "Access token is not expired!",
      });
    }
  } else {
    return res.status(400).send({
      ok: false,
      message: "Access token or refresh token invalid",
    });
  }
};

module.exports = { refresh };
