<template id="calorie-page">
  <app-layout>
    <div class="card bg-light mb-3">
      <div class="card-header">
        <div class="row">
          <div class="col-6">Calorie Recommendation</div>
          <div class="col" align="right">
            <a class="btn btn-sm btn-outline-secondary" href="/">Back</a>
          </div>
        </div>
      </div>

      <div class="card-body">

        <div v-if="error" class="alert alert-danger">
          {{ error }}
        </div>

        <div v-else>
          <div class="row g-3">

            <div class="col-md-4">
              <div class="card h-100">
                <div class="card-body">
                  <h6>Maintain</h6>
                  <h3>{{ maintenance }}</h3>
                  <p class="text-muted mb-0">Maintain current weight</p>
                </div>
              </div>
            </div>

            <div class="col-md-4">
              <div class="card h-100">
                <div class="card-body">
                  <h6>Cut</h6>
                  <h3>{{ cut }}</h3>
                  <p class="text-muted mb-0">Calorie deficit</p>
                </div>
              </div>
            </div>

            <div class="col-md-4">
              <div class="card h-100">
                <div class="card-body">
                  <h6>Bulk</h6>
                  <h3>{{ bulk }}</h3>
                  <p class="text-muted mb-0">Calorie surplus</p>
                </div>
              </div>
            </div>

          </div>

          <hr class="my-4"/>

          <p class="text-muted mb-0">
            BMR: <strong>{{ details.bmr }}</strong> kcal/day
          </p>
        </div>

      </div>
    </div>
  </app-layout>
</template>

<script>
app.component("calorie-page", {
  template: "#calorie-page",

  data: () => ({
    userId: null,
    error: null,
    maintenance: null,
    cut: null,
    bulk: null,
    details: {}
  }),

  created() {
    this.userId = this.$javalin.pathParams["user-id"];
    this.loadCalories();
  },

  methods: {
    loadCalories() {
      axios.get(`/api/users/${this.userId}/calories`)
          .then(res => {
            const d = res.data;

            this.details = {
              bmr: Number(d.bmr),
            };

            this.maintenance = Number(d.maintenanceCalories);
            this.cut = Number(d.deficitCalories);
            this.bulk = Number(d.surplusCalories);
          })
          .catch(err => {
            console.log(err);
            this.error = "Could not load calorie recommendation.";
          });
    }
  }
});
</script>
