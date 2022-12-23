const router = require("express").Router();
const db = require("../tools/db");

router.post("/save", async (req, res) => {
  try {
    const product_id = req.body.id;
    const type = req.body.type;
    const path = req.body.path;

    const [result] = await db.query(
      `INSERT into image(product_id, type, path) VALUES (${product_id}, ${type}, '${path}')`
    );

    res.json({ result });
  } catch (err) {
    console.error(err);
    res.status(500).json({
      message: "something went wrong",
    });
  }
});

router.get("/:id", async (req, res) => {
  try {
    const product_id = req.params.id;
    const [result] = await db.query(
      `SELECT path FROM image WHERE image.product_id = ${product_id}`
    );

    res.json({
      result,
    });
  } catch (err) {
    console.error(err);
    res.status(500).json({
      message: "something went wrong",
    });
  }
});

module.exports = router;
