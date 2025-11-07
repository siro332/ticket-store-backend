from django.urls import path

from users.views import views
from users.views.organization_view import OrganizationView

urlpatterns = [
    path('register/', views.register, name='register'),
    path('organization/create', OrganizationView.as_view(), name='create_organization'),
    path('organization/<int:organization_id>/', OrganizationView.as_view(), name='update_organization'),

    # Xóa tổ chức (DELETE)
    path('organization/<int:organization_id>/delete/', OrganizationView.as_view(), name='delete_organization'),
]
