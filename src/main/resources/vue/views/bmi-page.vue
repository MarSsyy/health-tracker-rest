<template id="bmi-page">
  <app-layout>
    <div class="card bg-light mb-3">
      <div class="card-header">
        <div class="row">
          <div class="col-6">BMI Result</div>
          <div class="col" align="right">
            <a class="btn btn-sm btn-outline-secondary" href="/">Back</a>
          </div>
        </div>
      </div>

      <div class="card-body">

        <div v-if="error" class="alert alert-danger">
          {{ error }}
        </div>

        <div v-else-if="bmi !== null" class="text-center">
          <h4 class="mb-3">
            Your BMI
          </h4>

          <h2 class="mb-2">
            <span class="badge bg-info text-dark">
              {{ bmi.toFixed(2) }}
            </span>
          </h2>

          <p class="mb-0">
            Category:
            <strong>{{ category }}</strong>
          </p>
        </div>

      </div>
    </div>
  </app-layout>
</template>

<script>
app.component("bmi-page", {
  template: "#bmi-page",

  data: () => ({
    bmi: null,
    category: "",
    error: ""
  }),

  created() {
    const userId = this.$javalin.pathParams["user-id"];

    axios.get(`/api/users/${userId}/bmi`)
        .then(res => {

          this.bmi = Number(res.data.bmi);
          this.category = this.getCategory(this.bmi);
        })
        .catch(err => {
          if (err.response?.status === 403) {
            this.error = "You can only view your own BMI.";
          } else if (err.response?.status === 400) {
            this.error = "Height and weight must be set.";
          } else if (err.response?.status === 404) {
            this.error = "User not found.";
          } else {
            this.error = "Unable to load BMI.";
          }
        });
  },

  methods: {
    getCategory(bmi) {
      if (bmi < 18.5) return "Underweight";
      if (bmi < 25) return "Normal weight";
      if (bmi < 30) return "Overweight";
      return "Obese";
    }
  }
});
</script>

