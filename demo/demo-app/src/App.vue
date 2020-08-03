<template>
  <q-layout class="q-ml-xl" style="width: 700px; max-width: 90vw;">
    <h5>Welcome, {{ subscriptionForm.userId }}</h5>
    <h4>Create A Subscription</h4>

    <form>

    <q-select
      emit-value
      class="q-mb-md"
      :label-width="labelWidth"
      label="Subscription Plan:"
      :error="$v.subscriptionForm.amount.$error"
      :error-message="amountHelper"
      v-model="subscriptionForm.amount"
      :options="chooseAmount"
      @blur="$v.subscriptionForm.amount.$touch();"
      @keyup.enter="submit"
    />

    <q-select
      emit-value
      class="q-mb-md"
      :label-width="labelWidth"
      label="Subscription Type:"
      :error="$v.subscriptionForm.type.$error"
      :error-message="typeHelper"
      v-model="subscriptionForm.type"
      :options="chooseType"
      @blur="$v.subscriptionForm.type.$touch();"
      @keyup.enter="submit"
    />

    <q-input
      class="q-mb-md"
      :label-width="labelWidth"
      label="Start Date (YYYY/MM/DD):"
      :error="$v.subscriptionForm.startDate.$error"
      :error-message="startDateErrorMessage()"
      v-model="subscriptionForm.startDate"
      mask="date"
      :rules="['date']"
      @blur="$v.subscriptionForm.startDate.$touch();"
      @keyup.enter="submit"
    >
      <template v-slot:append>
        <q-icon name="event" class="cursor-pointer">
          <q-popup-proxy ref="qStartDateProxy" transition-show="scale" transition-hide="scale">
            <q-date
              v-model="subscriptionForm.startDate"
              @input="() => $refs.qStartDateProxy.hide()"
              :options="startDateOptions"
            />
          </q-popup-proxy>
        </q-icon>
      </template>
    </q-input>

    <q-input
      :disable="disableEndDate()"
      class="q-mb-md"
      :label-width="labelWidth"
      label="End Date (YYYY/MM/DD):"
      :error="endDateHasError()"
      :error-message="endDateErrorMessage()"
      v-model="subscriptionForm.endDate"
      mask="date"
      :rules="['date']"
      @blur="$v.subscriptionForm.endDate.$touch();"
      @keyup.enter="submit"
    >
      <template v-slot:append>
        <q-icon name="event" class="cursor-pointer">
          <q-popup-proxy ref="qEndDateProxy" transition-show="scale" transition-hide="scale">
            <q-date
              v-model="subscriptionForm.endDate"
              @input="() => $refs.qEndDateProxy.hide()"
              :options="endDateOptions"
            />
          </q-popup-proxy>
        </q-icon>
      </template>
    </q-input>

    <q-btn color="blue" @click="submitForm()">Create</q-btn>

    </form>

    <q-dialog v-model="showOutput">
      <q-card>
        <q-card-section>
          <div class="text-h6">Your subscription is successful!</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          {{ outputAmount }}
        </q-card-section>

        <q-card-section class="q-pt-none">
          {{ outputType }}
        </q-card-section>

        <q-card-section class="q-pt-none q-mb-none">
          The following are your invoice dates:
        </q-card-section>

        <q-card-section class="q-pt-none" style="white-space: pre-wrap;">
          {{ outputDates }}
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="OK" color="primary" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>

  </q-layout>
</template>

<script>

import axios from 'axios';
import Vue from 'vue'
import Vuelidate from 'vuelidate'

Vue.use(
  Vuelidate,
);

import { required } from 'vuelidate/lib/validators'

const checkStartDate = 
  (date) => {
      let today = new Date().toISOString().slice(0, 10).replace(/-/g,'/');

      return date >= today;
  };

export default {

  data () {
    return {
      subscriptionForm: {
        userId: "Kai",
        amount: "",
        type: "",
        startDate: "",
        endDate: "",
      },

      amountHelper: "Select a plan",
      typeHelper: "Select a subscription type",

      labelWidth: 12,

      chooseAmount: [
        {label: 'Basic. $10.50', value: 10.50},
        {label: 'Basic. $50.00', value: 50.00},
      ],
      chooseType: [
        {label: 'Daily', value: "DAILY"},
        {label: 'Weekly', value: "WEEKLY"},
        {label: 'Monthly', value: "MONTHLY"},
      ],

      showOutput: false,
      outputAmount: "",
      outputType: "",
      outputDates: "",
    }
  },

  validations: {
    subscriptionForm: {
      amount: { required },
      type: { required },
      startDate: {
        required,
        checkStartDate,
      },
      endDate: { required },
    }
  },

  methods: {

    submitForm(){
        this.$v.$touch();

        if (this.$v.$invalid) {

            this.$q.notify({
                message: 'Please review fields again, make sure they have the correct information.',
                color: 'negative',
                icon: 'report_problem',
                position: 'top',
                actions: [{label: 'Close', }],
                timeout: 5000,
            });
          return;
        }

        let endDateParts =this.subscriptionForm.endDate.split('/');
        let endDate = new Date(endDateParts[0], endDateParts[1] - 1, endDateParts[2]);
        let startDateParts =this.subscriptionForm.startDate.split('/');
        let startDate = new Date(startDateParts[0], startDateParts[1] - 1, startDateParts[2]);

        axios.post(
                "http://localhost:8080/subscriptions",
                {
                    userId: this.subscriptionForm.userId,
                    amount: this.subscriptionForm.amount,
                    type: this.subscriptionForm.type,
                    startDate: startDate,
                    endDate: endDate,
                })
            .then(response => {

                this.subscriptionForm.endDate = "";
                this.subscriptionForm.startDate = "";
                this.subscriptionForm.amount = "";
                this.subscriptionForm.type = "";
                this.$v.$reset();

                this.outputAmount = "The amount charged for each invoice is $" + response.data.amount + ".";
                this.outputType = "This is a " + response.data.subscriptionType + " subscription.";
                this.outputDates = JSON.stringify(response.data.invoiceDates, null, '\t');
                this.showOutput = true;
            });
    },

    startDateOptions(date){
      return checkStartDate(date);
    },

    endDateOptions(date) {

      let endDateParts =date.split('/');
      let endDate = new Date(endDateParts[0], endDateParts[1] - 1, endDateParts[2]);
      let startDateParts =this.subscriptionForm.startDate.split('/');
      let startDate = new Date(startDateParts[0], startDateParts[1] - 1, startDateParts[2]);
      let difference = Math.floor((endDate - startDate) / (1000*60*60*24));

      if(difference < 0 || difference > 90) {
        return false;
      }

      return true;
    },

    startDateErrorMessage(){
        if(this.$v.subscriptionForm.startDate.$error){
          //required
          if(!this.$v.subscriptionForm.startDate.required){
            return "Enter a start date for your subscription";
          }

          //check for past dates
          if(!this.$v.subscriptionForm.startDate.checkStartDate){
            return "Past dates are invalid.";
          }
        }
    },

    endDateHasError(){
        if(this.$v.subscriptionForm.endDate.$error){
          return true;
        }

        if(!this.validateEndDateDifference()){
          return true;
        }

        return false;
    },
    
    disableEndDate(){
      if(!this.subscriptionForm.startDate || this.$v.subscriptionForm.startDate.$error){
        return true;
      }

      return false;
    },

    validateEndDateDifference(){
        if(this.subscriptionForm.endDate) {
          if(!this.endDateOptions(this.subscriptionForm.endDate)) {
            return false;
          }
        }

        return true;
    },

    endDateErrorMessage(){
        if(this.$v.subscriptionForm.endDate.$error){
          //required
          if(!this.$v.subscriptionForm.startDate.required){
            return "Enter an end date for your subscription";
          }
        }

        //check for difference
        if(!this.validateEndDateDifference()){
          return "The start date and end date cannot be more than 3 months apart.";
        }
    },
  },
}
</script>

<style>
</style>
