class SessionsController < ApplicationController
	respond_to :json
	def create
		auser = Auser.from_omniauth(env["omniauth.auth"])
		session[:auser_id] = auser.id
		msg = { :status => "ok", :message => "Success!", :uid => auser.uid, :provider => auser.provider, :email => auser.email, :name => auser.name }
    	render :json => msg
		# respond_with(auser)
	end
end
