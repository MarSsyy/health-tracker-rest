<template id="app-layout">
  <div class="app-layout">
    <div class="container">
      <!-- Start of navbar -->
      <nav class="navbar navbar-expand-lg navbar-light">
        <a class="navbar-brand" href="/">Home</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent"
                aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
          <ul class="navbar-nav me-auto">

            <li class="nav-item" v-if="user && user.role === 'ADMIN'">
              <a class="nav-link" href="/users">Users</a>
            </li>

            <li class="nav-item" v-if="user && user.role === 'ADMIN'">
              <a class="nav-link" href="/activities">Activities</a>
            </li>

            <li class="nav-item" v-if="user && user.role === 'USER'">
              <a class="nav-link" :href="`/users/${user.id}`">My Profile</a>
            </li>
          </ul>

        </div>

        <button class="btn btn-danger btn-sm" @click="logout">
          Logout
        </button>

      </nav>
      <!--End of nav bar-->
      <!--Start of main content area-->
      <div class="content mt-3">
        <div class="container-fluid">
          <slot></slot>
        </div>
      </div>
      <!--End of main content area-->
    </div>
  </div>
</template>

<script>
app.component("app-layout", {
  template: "#app-layout",
  data: () => ({
    user: null
  }),
  created() {
    axios.get("/api/auth/me")
        .then(res => {
          this.user = res.data;
        })
        .catch(() => {
          window.location.href = "/login";
        });
  },
  methods: {
    logout() {
      axios.post("/api/auth/logout")
          .then(() => window.location.href = "/login");
    }
  }
});
</script>


<style>
.navbar {
  background-color: #e3f2fd;
}
</style>

