<template id="user-activity-overview">
  <app-layout>
    <!-- Card -->
    <div class="card bg-light mb-3">
      <div class="card-header">
        <div class="row">
          <div class="col-6">
            User Activities
          </div>
          <div class="col" align="right">
            <button rel="tooltip" title="Add"
                    class="btn btn-info btn-simple btn-link"
                    @click="hideForm = !hideForm">
              <i class="fa fa-plus" aria-hidden="true"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- Add Activity Form -->
      <div class="card-body" :class="{ 'd-none': hideForm }">
        <form id="addActivity">
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text">Description</span>
            </div>
            <input type="text" class="form-control"
                   v-model="formData.description"
                   placeholder="Running"/>
          </div>

          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text">Duration (mins)</span>
            </div>
            <input type="number" class="form-control"
                   v-model.number="formData.duration"
                   placeholder="30"/>
          </div>

          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text">Started</span>
            </div>
            <input type="text" class="form-control"
                   v-model="formData.started"
                   placeholder="2025-12-23T07:30:00"/>
          </div>

          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text">Calories</span>
            </div>
            <input type="number" class="form-control"
                   v-model.number="formData.calories"
                   placeholder="250"/>
          </div>
        </form>

        <button class="btn btn-info btn-simple btn-link"
                @click="addActivity()">Add Activity</button>
      </div>
    </div>

    <!-- Activities List -->
    <div class="list-group list-group-flush">
      <div class="list-group-item d-flex align-items-start"
           v-for="(activity, index) in activities"
           :key="activity.id">
        <div class="mr-auto p-2">
          <span>
            {{ activity.description }}
            ({{ activity.duration }} mins, {{ activity.calories }} cal)
          </span>
          <div class="small text-muted" v-if="activity.started">
            Started: {{ activity.started }}
          </div>
        </div>

        <div class="p2">
          <button rel="tooltip" title="Delete"
                  class="btn btn-info btn-simple btn-link"
                  @click="deleteActivity(activity, index)">
            <i class="fas fa-trash"></i>
          </button>
        </div>
      </div>
    </div>
  </app-layout>
</template>

<script>
app.component("user-activity-overview", {
  template: "#user-activity-overview",
  data: () => ({
    activities: [],
    hideForm: true,
    userId: null,
    formData: {
      description: "",
      duration: null,
      started: "",
      calories: null
    }
  }),
  created() {
    this.userId = this.$javalin.pathParams["user-id"];
    this.fetchActivities();
  },
  methods: {
    fetchActivities() {
      axios.get(`/api/users/${this.userId}/activities`)
          .then(res => this.activities = res.data)
          .catch(() => alert("Error while fetching activities"));
    },

    addActivity() {
      axios.post("/api/activities", {
        description: this.formData.description,
        duration: this.formData.duration,
        started: this.formData.started,
        calories: this.formData.calories,
        userId: this.userId
      })
          .then(res => {
            this.activities.push(res.data);
            this.hideForm = true;

            // reset form
            this.formData = {
              description: "",
              duration: null,
              started: "",
              calories: null
            };
          })
          .catch(err => console.log(err));
    },

    deleteActivity(activity, index) {
      if (confirm("Are you sure you want to delete this activity?")) {
        axios.delete(`/api/activities/${activity.id}`)
            .then(() => this.activities.splice(index, 1))
            .catch(err => console.log(err));
      }
    }
  }
});
</script>
