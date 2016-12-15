class Auser < ApplicationRecord
	def self.from_omniauth(auth)
		where(provider: auth.provider, uid: auth.uid).first_or_create do |auser|
			auser.provider = auth.provider 
			auser.uid      = auth.uid
			auser.name     = auth.info.name
			auser.email    = auth.info.email
			auser.save
		end
	end
	
	# def self.from_omniauth(auth)
	# 	where(auth.slice("provider", "uid")).first || create_from_omniauth(auth)
		
	# end

	# def self.create_from_omniauth(auth)
	# 	create! do |auser|
	# 		auser.provider = auth["provider"]
	# 		auser.uid = auth["uid"]
	# 		auser.name = auth["info"]["name"]
	# 		auser.email = auth["info"]["email"]
	# 	end
	# end

	# private
	# def user_params
 #    	params.require(:auser).permit(:provider, :uid, :name, :email)
 #  	end
end
