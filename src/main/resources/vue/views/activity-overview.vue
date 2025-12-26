<template id="activity-overview">
  <app-layout>
    <div class="card bg-light mb-3">
      <div class="card-header">
        <div class="row">
          <div class="col-6">
            Activities
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
                class="btn btn-info btn-simple btn-link"
                @click="addActivity()">Add Activity</button>
      </div>
    </div>

    <div class="list-group list-group-flush">
      <div class="list-group-item d-flex align-items-start"
           v-for="(activity, index) in activities" :key="activity.id">
        <div class="mr-auto p-2">
          <span>
            <a :href="`/users/${activity.userId}/activities`">
              {{ activity.description }}
              ({{ activity.duration }} mins, {{ activity.calories }} cal)
              - User {{ activity.userId }}
            </a>
          </span>
          <div class="small text-muted" v-if="activity.started">
            Started: {{ activity.started }}
          </div>
        </div>

        <div class="p2">

          <a :href="`/users/${activity.userId}/activities`">
            <button rel="tooltip" title="View User"
                    class="btn btn-info btn-simple btn-link">
              <i class="fa fa-pencil" aria-hidden="true"></i>
            </button>
          </a>

          <button rel="tooltip" title="Delete"
                  class="btn btn-info btn-simple btn-link"
                  @click="deleteActivity(activity, index)">
            <i class="fas fa-trash" aria-hidden="true"></i>
          </button>
        </div>
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
      if (confirm("Are you sure you want to delete this activity? This action cannot be undone.", "Warning")) {
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
          })
          .catch(error => console.log(error));
    }
  }
});
</script>

