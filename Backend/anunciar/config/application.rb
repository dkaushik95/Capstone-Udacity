require_relative 'boot'
ENV['RAILS_ADMIN_THEME'] = 'rollincode'
require 'rails/all'
ENV["RAILS_ASSET_ID"] = ''

# Require the gems listed in Gemfile, including any gems
# you've limited to :test, :development, or :production.
Bundler.require(*Rails.groups)


module Anunciar
  class Application < Rails::Application
    # Settings in config/environments/* take precedence over those specified here.
    # Application configuration should go into files in config/initializers
    # -- all .rb files in that directory are automatically loaded.
  end
end
