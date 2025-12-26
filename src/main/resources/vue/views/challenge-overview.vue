<template id="challenge-overview">
  <app-layout>
    <div class="card bg-light mb-3">
      <div class="card-header">
        <div class="row">
          <div class="col-6">Challenges</div>
          <div class="col" align="right">
            <a class="btn btn-sm btn-outline-secondary" href="/">Back</a>
          </div>
        </div>
      </div>

      <div class="card-body p-0">
        <div v-if="challenges.length === 0" class="p-3 text-muted">
          No challenges found.
        </div>

        <div v-else class="list-group list-group-flush">
          <div class="list-group-item" v-for="c in challenges" :key="c.id">
            <div><strong>{{ c.name }}</strong></div>
            <div class="text-muted small">{{ c.description }}</div>
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
    challenges: []
  }),
  created() {
    axios.get("/api/challenges")
        .then(res => this.challenges = res.data)
        .catch(() => alert("Could not load challenges"));
  }
});
</script>
