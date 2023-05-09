const { createApp } = Vue;
const app = createApp({
    data() {
        return {
            data: [],
            firstName: "",
            lastName: "",
            email: "",
        }
    },
    created() {
        axios.get('http://localhost:8080/rest/clients')
            .then(response => {
                this.data = response.data._embedded.clients
                console.log(this.data)
            })
            .catch(err => console.log(err))
    },
    methods: {
        addClient() {
            axios({
                method: 'post',
                url: 'http://localhost:8080/rest/clients',
                data: {
                    firstName: this.firstName,
                    lastName: this.lastName,
                    email: this.email,
                }
            })
        }
    }
})
app.mount('#app')