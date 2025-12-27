<template id="activity-overview">
  <app-layout>
    <div class="card bg-light mb-3">
      <div class="card-header">
        <div class="row">
          <div class="col-6">
            Add Activity
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

      <div class="card-body" :class="{ 'd-none': hideForm }">
        <form id="addActivity">
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-activity-description">Description</span>
            </div>
            <input type="text" class="form-control" v-model="formData.description"
                   name="description" placeholder="e.g., Running"/>
          </div>

          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-activity-duration">Duration (mins)</span>
            </div>
            <input type="number" class="form-control" v-model.number="formData.duration"
                   name="duration" placeholder="30"/>
          </div>

          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-activity-started">Started</span>
            </div>

            <input type="text" class="form-control" v-model="formData.started"
                   name="started" placeholder="2025-12-23T07:30:00"/>
          </div>

          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-activity-calories">Calories</span>
            </div>
            <input type="number" class="form-control" v-model.number="formData.calories"
                   name="calories" placeholder="250"/>
          </div>

          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-activity-userId">User ID</span>
            </div>
            <input type="number" class="form-control" v-model.number="formData.userId"
                   name="userId" placeholder="1"/>
          </div>
        </form>

        <button rel="tooltip" title="Add"
                class="btn btn-primary"
                @click="addActivity()">Add Activity</button>
      </div>
    </div>

    <div class="card bg-light mb-3">
      <div class="card-header d-flex justify-content-between align-items-center">
        Activity List
        <a class="btn btn-sm btn-outline-secondary" href="/">Back</a>
      </div>

      <div class="card-body p-0">
        <ul class="list-group list-group-flush">

          <li class="list-group-item d-flex align-items-center"
              v-for="(activity, index) in activities"
              :key="activity.id">

            <div class="flex-grow-1">
              <div class="font-weight-bold">
                {{ activity.description }}
              </div>

              <div class="text-muted small">
                {{ activity.duration }} mins · {{ activity.calories }} cal
                <span v-if="activity.started">· Started: {{ activity.started }}</span>
              </div>

              <div class="small">
                User ID: {{ activity.userId }}
              </div>
            </div>

            <div class="btn-group">
              <button class="btn btn-sm btn-outline-danger"
                      title="Delete Activity"
                      @click="deleteActivity(activity, index)">
                <i class="fas fa-trash"></i>
              </button>
            </div>

          </li>

          <li v-if="activities.length === 0"
              class="list-group-item text-center text-muted">
            No activities found.
          </li>

        </ul>
      </div>
    </div>

  </app-layout>
</template>

<script>
app.component("activity-overview", {
  template: "#activity-overview",
  data: () => ({
    activities: [],
    formData: {
      description: "",
      duration: null,
      started: "",
      calories: null,
      userId: null
    },
    hideForm: true
  }),
  created() {
    this.fetchActivities();
  },
  methods: {
    fetchActivities() {
      axios.get("/api/activities")
          .then(res => this.activities = res.data)
          .catch(() => alert("Error while fetching activities"));
    },

    deleteActivity(activity, index) {
      if (confirm("Are you sure you want to delete this activity?")) {
        const url = `/api/activities/${activity.id}`;
        axios.delete(url)
            .then(() => {
              this.activities.splice(index, 1);
            })
            .catch(error => console.log(error));
      }
    },

    addActivity() {
      const url = "/api/activities";

      axios.post(url, {
        description: this.formData.description,
        duration: this.formData.duration,
        started: this.formData.started,
        calories: this.formData.calories,
        userId: this.formData.userId
      })
          .then(response => {
            this.activities.push(response.data);
            this.hideForm = true;

            // reset form
            this.formData = { description: "", duration: null, started: "", calories: null, userId: null };
            window.location.reload();
          })
          .catch(error => console.log(error));
    }
  }
});
</script>

