<template id="challenge-overview">
  <app-layout>

    <div class="card bg-light mb-3" v-if="me && me.role === 'ADMIN'">
      <div class="card-header">
        <div class="row">
          <div class="col-6">Add Challenge</div>
          <div class="col" align="right">
            <button rel="tooltip" title="Add"
                    class="btn btn-info btn-simple btn-link"
                    @click="hideForm = !hideForm">
              <i class="fa fa-plus" aria-hidden="true"></i>
            </button>
          </div>
        </div>
      </div>

      <div class="card-body" :class="{ 'd-none': hideForm }">
        <form id="addChallenge">
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text">Name</span>
            </div>
            <input type="text"
                   class="form-control"
                   v-model="formData.name"
                   placeholder="e.g., 30 Day Run"/>
          </div>

          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text">Description</span>
            </div>
            <input type="text"
                   class="form-control"
                   v-model="formData.description"
                   placeholder="Run 3km every day"/>
          </div>
        </form>

        <button class="btn btn-primary" @click.prevent="addChallenge">
          Add Challenge
        </button>
      </div>
    </div>

    <div class="card bg-light mb-3">
      <div class="card-header">
        Challenges
      </div>

      <div class="card-body p-0">
        <div v-if="challenges.length === 0" class="p-3 text-muted">
          No challenges found.
        </div>

        <div v-else class="list-group list-group-flush">

          <div class="list-group-item d-flex justify-content-between align-items-start"
               v-for="c in challenges"
               :key="c.id">

            <div>
              <div><strong>{{ c.name }}</strong></div>
              <div class="text-muted small">{{ c.description }}</div>
            </div>

            <div v-if="me && me.role === 'ADMIN'">
              <button class="btn btn-sm btn-outline-danger"
                      title="Delete"
                      @click="deleteChallenge(c)">
                <i class="fas fa-trash"></i>
              </button>
            </div>

          </div>

        </div>
      </div>
    </div>

  </app-layout>
</template>

<script>
app.component("challenge-overview", {
  template: "#challenge-overview",

  data: () => ({
    challenges: [],
    me: null,

    hideForm: true,

    formData: {
      name: "",
      description: ""
    }
  }),

  created() {

    axios.get("/api/auth/me")
        .then(response => {
          this.me = response.data;
        })
        .catch(() => {
          this.me = null;
        });

    this.fetchChallenges();
  },

  methods: {

    fetchChallenges() {
      axios.get("/api/challenges")
          .then(response => {
            this.challenges = response.data;
          })
          .catch(() => {
            alert("Could not load challenges");
          });
    },

    addChallenge() {

      // Simple validation
      if (this.formData.name === "" || this.formData.description === "") {
        alert("Please enter name and description");
        return;
      }

      axios.post("/api/challenges", {
        name: this.formData.name,
        description: this.formData.description
      })
          .then(() => {
            window.location.reload();
          })
          .catch(() => {
            alert("Failed to add challenge");
          });
    },

    deleteChallenge(challenge) {

      if (!confirm(`Delete "${challenge.name}" ?`)) {
        return;
      }

      axios.delete(`/api/challenges/${challenge.id}`)
          .then(() => {
            this.challenges = this.challenges.filter(
                c => c.id !== challenge.id
            );
          })
          .catch(() => {
            alert("Failed to delete challenge");
          });
    }
  }
});
</script>

