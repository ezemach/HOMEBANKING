const { createApp } = Vue;

const app = createApp({
    data() {
        return {
            data: [],
            transacciones: [],
            id: new URLSearchParams(location.search).get('id'),

        };
    },
    created(){
        axios.get('http://localhost:8080/api/accounts/'+ this.id)
            .then(response => {
                this.data = response.data
                this.transacciones = this.data.transactions;
                this.transacciones.sort((a, b) => b.id - a.id)
                console.log(this.data);
                console.log(this.transacciones);

            })
            .catch(err => console.log(err))

},

});

app.mount('#app')

