db = db.getSiblingDB("app_db");

db.createUser({
  user: "app_user",
  pwd: "app_password",
  roles: [
    { role: "dbOwner", db: "app_db" }
  ]
});
