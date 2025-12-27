<!-- home-page -->
<template id="home-page">
  <app-layout>
    <div v-if="me">

      <div v-if="me.role === 'ADMIN'" class="row mb-3">
        <div class="col">
          <div class="card">
            <h5 class="card-header">Registered Users</h5>
            <div class="card-body">
              <h5 class="card-title">{{ users.length }} users</h5>
              <a href="/users" class="btn btn-primary">More Details...</a>
            </div>
          </div>
        </div>

        <div class="col">
          <div class="card">
            <h5 class="card-header">Total Activities</h5>
            <div class="card-body">
              <h5 class="card-title">{{ activities.length }} activities</h5>
              <a href="/activities" class="btn btn-primary">More Details...</a>
            </div>
          </div>
        </div>

        <div class="col">
          <div class="card">
            <h5 class="card-header">Total Challenges</h5>
            <div class="card-body">
              <h5 class="card-title">{{ challenges.length }} challenges</h5>
              <a href="/challenges" class="btn btn-primary">More Details...</a>
            </div>
          </div>
        </div>


      </div>


      <div v-if="me.role === 'USER'">

        <div class="row mb-3">
          <div class="col-md-4">
            <div class="card">
              <h5 class="card-header">BMI Calculator</h5>
              <div class="card-body">
                <p class="card-text">Check your Body Mass Index</p>
                <a :href="`/users/${me.id}/bmi`" class="btn btn-primary">Calculate BMI</a>
              </div>
            </div>
          </div>

          <div class="col-md-4">
            <div class="card">
              <h5 class="card-header">Calories Recommendation</h5>
              <div class="card-body">
                <p class="card-text">Daily calorie needs</p>
                <a :href="`/users/${me.id}/calories`" class="btn btn-primary">View Calories</a>
              </div>
            </div>
          </div>

          <div class="col-md-4">
            <div class="card">
              <h5 class="card-header">Track Activities</h5>
              <div class="card-body">
                <p class="card-text">Track your activities</p>
                <a :href="`/users/${me.id}/activities`" class="btn btn-primary">View</a>
              </div>
            </div>
          </div>
        </div>


        <div class="row">
          <div class="col-12">
            <div class="card bg-light">
              <div class="card-header">
                <div class="row">
                  <div class="col-6">Challenges</div>

                  <div class="col" align="right">
                    <button v-if="me.role === 'ADMIN'"
                            class="btn btn-primary btn-sm"
                            @click="goToAddChallenge()">
                      Add Challenge
                    </button>
                  </div>
                </div>
              </div>

              <div class="card-body p-0">
                <div v-if="challenges.length === 0" class="p-3 text-muted">
                  No available challenges at the moment.
                </div>

                <ul v-else class="list-group list-group-flush">
                  <li class="list-group-item d-flex justify-content-between align-items-start"
                      v-for="challenge in challenges"
                      :key="challenge.id">

                    <div>
                      <strong>{{ challenge.name }}</strong>
                      <div class="text-muted small">{{ challenge.description }}</div>
                    </div>


                    <button v-if="me.role === 'USER'"
                            class="btn btn-sm"
                            :class="challenge.joined ? 'btn-secondary' : 'btn-success'"
                            :disabled="challenge.joined || challenge.joinLoading"
                            @click="joinChallenge(challenge)">
                      <span v-if="challenge.joinLoading">Joining...</span>
                      <span v-else>{{ challenge.joined ? "Joined" : "Join" }}</span>
                    </button>
                  </li>
                </ul>

              </div>
            </div>
          </div>
        </div>

      </div>

    </div>
  </app-layout>
</template>

<script>
app.component("home-page", {
  template: "#home-page",
  data: () => ({
    me: null,
    users: [],
    activities: [],
    challenges: []
  }),

  created() {
    axios.get("/api/auth/me")
        .then(res => {
          this.me = res.data;

          if (this.me.role === "ADMIN") {
            axios.get("/api/users").then(r => this.users = r.data);
            axios.get("/api/activities").then(r => this.activities = r.data);
          }


          axios.get("/api/challenges")
              .then(r => {

                this.challenges = (r.data || []).map(c => ({
                  ...c,
                  joined: false,
                  joinLoading: false
                }));
              })
              .catch(() => this.challenges = []);
        })
        .catch(() => {
          this.me = null;
        });
  },

  methods: {
    goToAddChallenge() {
      window.location.href = "/challenges";
    },

    joinChallenge(challenge) {
      if (!this.me || !this.me.id) return;

      challenge.joinLoading = true;

      axios.post(`/api/users/${this.me.id}/challenges/${challenge.id}`)
          .then(() => {
            challenge.joined = true;
          })
          .catch(err => {

            if (err.response && err.response.status === 409) {
              challenge.joined = true;
            } else {
              alert(err.response?.data?.message ?? "Join failed");
            }
          })
          .finally(() => {
            challenge.joinLoading = false;
          });
    }
  }
});
</script>


