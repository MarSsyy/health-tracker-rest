<template id="login-page">
  <div class="d-flex justify-content-center align-items-center vh-100">
    <div class="card p-4 shadow" style="width: 420px;">
      <h3 class="text-center mb-4">Login</h3>

      <div class="mb-3">
        <label class="form-label">Email</label>
        <input v-model="email" class="form-control" type="email" />
      </div>

      <div class="mb-3">
        <label class="form-label">Password</label>
        <input v-model="password" class="form-control" type="password" />
      </div>
      <div class="mb-3">
        <span>Don’t have an account? </span>
        <a href="/signup">Sign up</a>
      </div>
      <button class="btn btn-primary w-100" @click="login">Login</button>
    </div>
  </div>
</template>


<script>
app.component("login-page", {
  template: "#login-page",
  data: () => ({ email: "", password: "" }),
  methods: {
    login() {
      axios.post("/api/auth/login", {
        email: this.email,
        password: this.password
      })
          .then(res => {
            console.log("LOGIN OK", res.data);

            // optional: keep user info for UI display
            localStorage.setItem("user", JSON.stringify(res.data));
            window.location.href = "/";

          })
          .catch(err => {
            console.log("LOGIN FAIL", err.response?.status, err.response?.data);
            alert("Invalid email or password");
          });
    }
  }
});
</script>

