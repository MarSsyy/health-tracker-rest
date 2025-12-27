<template id="user-overview">
  <app-layout>
    <div class="card bg-light mb-3">
      <div class="card-header">
        <div class="row">
          <div class="col-6">
            Add User
          </div>
          <div class="col" align="right">
            <button rel="tooltip" title="Add"
                    class="btn btn-info btn-simple btn-link"
                    @click="hideForm =!hideForm">
              <i class="fa fa-plus" aria-hidden="true"></i>
            </button>
          </div>
        </div>
      </div>
      <div class="card-body" :class="{ 'd-none': hideForm}">
        <form id="addUser">
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-user-name">Name</span>
            </div>
            <input type="text" class="form-control" v-model="formData.name" name="name" placeholder="Name"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-user-email">Email</span>
            </div>
            <input type="email" class="form-control" v-model="formData.email" name="email" placeholder="Email"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text">Password</span>
            </div>
            <input type="password" class="form-control" v-model="formData.password" name="password" placeholder="Password"/>
          </div>
        </form>
        <button type="button" class="btn btn-primary" @click="addUser">Add User</button>
      </div>
    </div>

    <div class="card bg-light mb-3">
      <div class="card-header d-flex justify-content-between align-items-center">
        Users List
        <a class="btn btn-sm btn-outline-secondary" href="/">Back</a>
      </div>

      <div class="card-body p-0">
        <ul class="list-group list-group-flush">
          <li class="list-group-item d-flex align-items-center"
              v-for="(user, index) in users"
              :key="user.id">

            <div class="flex-grow-1">
              <a :href="`/users/${user.id}`" class="font-weight-bold">
                {{ user.name }}
              </a>
              <div class="text-muted small">
                {{ user.email }}
              </div>
            </div>

            <div class="btn-group">
              <a :href="`/users/${user.id}`"
                 class="btn btn-sm btn-outline-info"
                 title="Update">
                <i class="fa fa-pencil"></i>
              </a>

              <button class="btn btn-sm btn-outline-danger"
                      title="Delete"
                      @click="deleteUser(user, index)">
                <i class="fas fa-trash"></i>
              </button>
            </div>

          </li>

          <li v-if="users.length === 0"
              class="list-group-item text-center text-muted">
            No users found
          </li>
        </ul>
      </div>
    </div>

  </app-layout>
</template>
<script>
app.component("user-overview", {
  template: "#user-overview",
  data: () => ({
    users: [],
    formData: [],
    hideForm :true,
  }),
  created() {
    this.fetchUsers();
  },
  methods: {
    fetchUsers: function () {
      axios.get("/api/users")
          .then(res => this.users = res.data)
          .catch(() => alert("Error while fetching users"));
    },
    deleteUser: function (user, index) {
      if (confirm('Are you sure you want to delete this user?')) {
        //user confirmed delete
        const userId = user.id;
        const url = `/api/users/${userId}`;
        axios.delete(url)
            .then(response =>
                //delete from the local state so Vue will reload list automatically
                this.users.splice(index, 1).push(response.data))
            .catch(function (error) {
              console.log(error)
            });
      }
    },
    addUser: function (){
      const url = `/api/users`;
      axios.post(url,
          {
            name: this.formData.name,
            email: this.formData.email,
            password: this.formData.password
          })
          .then(response => {
            this.users.push(response.data)
            this.hideForm= true;
          })
          .catch(error => {
            console.log(error)
          })
    }
  }
});
</script>
