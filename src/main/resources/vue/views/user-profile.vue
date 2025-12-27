<template id="user-profile">
  <app-layout>
    <div v-if="noUserFound">
      <p>We're sorry, we were not able to retrieve this user.</p>
      <p>Go back <a href="/">Home</a>.</p>
    </div>

    <div class="card bg-light mb-3" v-if="!noUserFound && user">
      <div class="card-header">
        <div class="row">
          <div class="col-6">User Profile</div>

          <div class="col" align="right">

            <button rel="tooltip" title="Update"
                    class="btn btn-info btn-simple btn-link"
                    @click="updateUser()">
              <i class="far fa-save" aria-hidden="true"></i>
            </button>

            <button v-if="isAdmin"
                    rel="tooltip" title="Delete"
                    class="btn btn-info btn-simple btn-link"
                    @click="deleteUser()">
              <i class="fas fa-trash" aria-hidden="true"></i>
            </button>
          </div>
        </div>
      </div>

      <div class="card-body">
        <form>

          <div class="input-group mb-3" v-if="isAdmin">
          <span class="input-group-text">User ID</span>
            <input class="form-control" v-model.number="user.id" type="number" readonly />
          </div>

          <div class="input-group mb-3">
            <span class="input-group-text">Name</span>
            <input class="form-control" v-model="user.name" type="text" />
          </div>

          <div class="input-group mb-3">
            <span class="input-group-text">Email</span>
            <input class="form-control" v-model="user.email" type="email" />
          </div>

          <div class="input-group mb-3">
            <span class="input-group-text">Password</span>
            <input class="form-control" v-model="user.password" type="password" />
          </div>

          <div class="input-group mb-3">
            <span class="input-group-text">Age</span>
            <input class="form-control" v-model.number="user.age" type="number" placeholder="e.g. 30" />
          </div>

          <div class="input-group mb-3">
            <span class="input-group-text">Gender</span>
            <select class="form-select" v-model="user.gender">
              <option disabled value="">Select...</option>
              <option value="male">male</option>
              <option value="female">female</option>
            </select>
          </div>

          <div class="input-group mb-3">
            <span class="input-group-text">Height (m)</span>
            <input class="form-control"
                   v-model.number="user.height"
                   type="number"
                   step="0.01"
                   placeholder="e.g. 1.75" />
          </div>

          <div class="input-group mb-3">
            <span class="input-group-text">Weight (kg)</span>
            <input class="form-control" v-model.number="user.weight" type="number" placeholder="e.g. 80" />
          </div>

          <div class="input-group mb-3">
            <span class="input-group-text">Activity</span>
            <select class="form-select" v-model="user.activityLevel">
              <option disabled value="">Select...</option>
              <option value="LIGHT">LIGHT</option>
              <option value="MODERATE">MODERATE</option>
              <option value="ACTIVE">ACTIVE</option>
            </select>
          </div>

          <div class="input-group mb-3" v-if="isAdmin">
            <div class="input-group-prepend">
              <span class="input-group-text">Role</span>
            </div>

            <select class="form-control" v-model="user.role" name="role">
              <option value="USER">USER</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </div>

        </form>
      </div>


    </div>
  </app-layout>
</template>

<script>
app.component("user-profile", {
  template: "#user-profile",
  data: () => ({
    user: null,
    activities: [],
    noUserFound: false,
    authUser: null
  }),

  created() {
    this.getLoggedInUser();
    this.loadUser();
  },

  computed: {
    isAdmin() {
      return this.authUser && this.authUser.role === "ADMIN";
    }
  },

  methods: {
    loadUser() {
      const userId = this.$javalin.pathParams["user-id"];
      const url = `/api/users/${userId}`;

      axios.get(url)
          .then(res => {
            this.user = res.data;
          })
          .catch(() => {
            this.noUserFound = true;
          });

      axios.get(`${url}/activities`)
          .then(res => this.activities = res.data)
          .catch(() => { /* ok if none */ });
    },

    getLoggedInUser() {
      axios.get("/api/auth/me")
          .then((res) => {
            this.authUser = res.data;
          })
          .catch(() => {
            this.authUser = null;
          });
    },

    updateUser() {
      const userId = this.$javalin.pathParams["user-id"];
      const url = `/api/users/${userId}`;

      // simple validation
      if (this.user.height != null && this.user.height > 0 && this.user.height < 0.5) {
        alert("Height looks too small. Use meters like 1.75");
        return;
      }

      axios.patch(url, {
        id: this.user.id,
        name: this.user.name,
        email: this.user.email,
        password: this.user.password,
        age: this.user.age,
        gender: this.user.gender,
        height: this.user.height,
        weight: this.user.weight,
        activityLevel: this.user.activityLevel,
        role: this.user.role
      })
          .then(() => {
            alert("User updated!");
            this.loadUser(); // PATCH returns 204 so re-fetch
          })
          .catch(err => {
            console.log(err);
            alert("Update failed");
          });
    },

    deleteUser() {
      if (!confirm("Do you really want to delete this user?")) return;

      const userId = this.$javalin.pathParams["user-id"];
      const url = `/api/users/${userId}`;

      axios.delete(url)
          .then(() => {
            alert("User deleted");
            window.location.href = "/users";
          })
          .catch(err => {
            console.log(err);
            alert("Delete failed");
          });
    },
  }
});
</script>

