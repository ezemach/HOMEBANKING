const { createApp } = Vue;
createApp({
    data() {
        return {
            data:[],
            loans:"",
            idLoan:"",
            amount:"",
            payments:"",
            personalPayments:"",
            hipotecarioPayments:"",
            automotrizPayments:"",
            numberAccount:"",
            filtrarId2:"",
            accounts:"",
            selectedLoan:"",
        }
    },
    created() {
      this.loadData(),
      this.loadClientData()
  },
    methods: {
        loadData(){
          axios.get('http://localhost:8080/api/loans')
          .then(response => {
              this.data = response.data
              console.log(this.data)
              this.personalPayments = response.data[0].payments
              console.log(this.payments)
              // this.filtrarId2 = this.data.filter(loan => {
              //      return this.typeLoan.includes(loan.name) 
              //  })[0];
              // console.log(this.filtrarId2)
          })
          .catch(err => console.log(err))
        },
        loadClientData(){
          axios.get('http://localhost:8080/api/clients/current')
          .then(response => {
              this.accounts = response.data.accounts
              console.log(this.accounts)
          })
          .catch(err => console.log(err))
        },

        perPay(){ 
        this.personalPayments = true,
        this.hipotecarioPayments= false,
        this.automotrizPayments= false
      },
        autoPay(){
          this.personalPayments = false,
          this.hipotecarioPayments= false,
          this.automotrizPayments= true
      },
        hipPay(){
        this.personalPayments = false,
        this.hipotecarioPayments= true,
        this.automotrizPayments= false
        },
        // getAccountNameById(id) {
        //   const account = this.accounts.find(acc => acc.id === id);
        //   return account ? account.name : '';
        // },
        // filtrarId(){
        //   this.selectedLoan = this.data.find(loan => {
        //     return this.typeLoan.includes(loan.name);
        //   });
        // },
        
        // filtrarId(){
        //   this.filtrarId2 = this.data.filter(loan => {
        //     return this.typeLoan.includes(loan.name) 
        //   })[0];

        // },
        newLoan() {
           console.log({
              id_Loan: this.idLoan,
              amount: this.amount,
              payments: this.payments,
              number: this.numberAccount
          })
            axios.post('/api/loans',{
              id_Loan: this.idLoan,
              amount: this.amount,
              payments: this.payments,
              number: this.numberAccount
          })
                .then(response => { 
                    console.log(response.data)
                    Swal.fire(
                        response.data,
                        'You have created a Loan!',
                        'success'
                      )
                    console.log("Loan")
                    console.log(response.data)
                    
                    
                })
                .catch(err =>{
                    Swal.fire({
                        icon: 'error',
                        title: 'Oops...',
                        text: err.response.data,
                      })
                console.error(err);
                this.error = "failed to create transaction. Please try again!"    
            });     
        },
        confirmLoan() {
          Swal.fire({
              title: 'Do You Confirm Your Loan?',
              html: `
                  <p>Loan selected: ${this.data[0].name}</p>
                  <p>Payments: ${this.payments}</p>
                  <p>Amount to pay: ${this.amount * 1.20}</p>
                  <p>Account to transver: ${this.numberAccount}</p>
              `,
              showCancelButton: true,
              confirmButtonText: 'Confirm',
              cancelButtonText: 'Cancel'
          }).then((result) => {
              if (result.isConfirmed) {
                this.newLoan()
              }
          })
      },
      
        logout() {
          axios.post('/api/logout')
          .then(response => {
                      window.location.replace('./index.html')
              })
              .catch(err => console.log(err));},
    }
  })
.mount('#app')

