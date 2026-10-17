import express from "express";
import { createUser, findUserByEmail } from "./db.js";
import { hashPassword, verifyPassword } from "./password.js";

const app = express();
const PORT = process.env.PORT || 4000;
const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

app.use(express.json());

app.post("/api/register", (req, res) => {
  const name = String(req.body?.name ?? "").trim();
  const email = String(req.body?.email ?? "").trim().toLowerCase();
  const password = String(req.body?.password ?? "");

  if (!name || !email || !password) {
    return res.status(400).json({ message: "Name, email, and password are required." });
  }
  if (!EMAIL_PATTERN.test(email)) {
    return res.status(400).json({ message: "Enter a valid email." });
  }
  if (password.length < 8) {
    return res.status(400).json({ message: "Use at least 8 characters for the password." });
  }
  if (findUserByEmail(email)) {
    return res.status(409).json({ message: "This email is already registered." });
  }

  const user = createUser(name, email, hashPassword(password));
  res.status(201).json({ message: "Account created.", user });
});

app.post("/api/login", (req, res) => {
  const email = String(req.body?.email ?? "").trim().toLowerCase();
  const password = String(req.body?.password ?? "");

  const user = findUserByEmail(email);
  if (!user || !verifyPassword(password, user.password_hash)) {
    return res.status(401).json({ message: "Email or password is incorrect." });
  }

  res.json({
    message: "Login successful.",
    user: { id: user.id, name: user.name, email: user.email },
  });
});

app.listen(PORT, (error) => {
  if (error) throw error;
  console.log(`MamaCare backend running at http://localhost:${PORT}`);
});
