Rails.application.routes.draw do
  get "/application.manifest" => Rails::Offline
  devise_for :admins
  devise_for :students
  mount RailsAdmin::Engine => '/admin', as: 'rails_admin'
  resources :announcements
  root 'announcements#index'
  namespace :api, defaults: {format: 'json'} do
  	namespace :v1 do
  		resources :announcements

  	end
  end
  get "/auth/google_oauth2/callback", to: 'sessions#create'


  # For details on the DSL available within this file, see http://guides.rubyonrails.org/routing.html
end
