<template id="signup-page">
  <div class="d-flex justify-content-center align-items-center"
       style="min-height: 100vh;">

    <div class="card p-4 shadow" style="width: 100%; max-width: 420px;">
      <div class="card-body">

        <h4 class="text-center mb-3">Create Account</h4>

        <div v-if="error" class="alert alert-danger">
          {{ error }}
        </div>

        <form @submit.prevent="signup">

          <label>Name</label>
          <input class="form-control"
                 v-model="name"
                 required />

          <label class="mt-2">Email</label>
          <input class="form-control"
                 v-model="email"
                 type="email"
                 required />

          <label class="mt-2">Password</label>
          <input class="form-control"
                 v-model="password"
                 type="password"
                 required />

          <button class="btn btn-primary mt-3 w-100">
            Sign up
          </button>

          <div class="mt-3 text-center">
            <span>Already have an account? </span>
            <a href="/login">Login</a>
          </div>

        </form>
      </div>
    </div>

  </div>
</template>



<script>
app.component("signup-page", {
  template: "#signup-page",

  data() {
    return {
      name: "",
      email: "",
      password: "",
      error: null
    }
  },

  methods: {
    async signup() {
      this.error = null

      try {

        await axios.post("/api/users", {
          id: 0,
          name: this.name,
          email: this.email,
          password: this.password,
          role: "USER"
        })

        await axios.post("/api/auth/login", {
          email: this.email,
          password: this.password
        })

        window.location.href = "/"

      } catch (err) {
        this.error = "Signup failed. Email may already exist."
      }
    }
  }
})
</script>
