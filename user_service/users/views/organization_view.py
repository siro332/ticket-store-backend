from django.http import JsonResponse
from rest_framework.exceptions import NotFound
from rest_framework.views import APIView
from rest_framework.permissions import IsAuthenticated

from users.models import Organization


class OrganizationView(APIView):
    permission_classes = [IsAuthenticated]  # Chỉ cho phép người dùng đã đăng nhập

    def post(self, request):
        # Kiểm tra xem người dùng có phải là admin hoặc organizer không
        if request.user.role not in ['admin', 'organizer']:
            return JsonResponse({"message": "You do not have permission to create an organization."}, status=403)

        # Lấy các trường từ request data
        name = request.data.get("name")
        description = request.data.get("description", "")
        address = request.data.get("address", "")
        established_date = request.data.get("established_date", None)

        # Kiểm tra xem tên tổ chức có được cung cấp không
        if not name:
            return JsonResponse({"message": "Organization name is required"}, status=400)

        # Tạo tổ chức mới
        organization = Organization.objects.create(
            name=name,
            description=description,
            address=address,
            established_date=established_date
        )

        return JsonResponse({"message": "Organization created successfully", "organization_id": organization.id})

    # PUT: Cập nhật tổ chức
    def put(self, request, organization_id):
        if request.user.role not in ['admin', 'organizer']:
            return JsonResponse({"message": "You do not have permission to update an organization."}, status=403)

        try:
            organization = Organization.objects.get(id=organization_id)
        except Organization.DoesNotExist:
            raise NotFound({"message": "Organization not found"})

        # Cập nhật các trường từ request data
        organization.name = request.data.get("name", organization.name)
        organization.description = request.data.get("description", organization.description)
        organization.address = request.data.get("address", organization.address)
        organization.established_date = request.data.get("established_date", organization.established_date)

        organization.save()

        return JsonResponse({"message": "Organization updated successfully", "organization_id": organization.id})

    # DELETE: Xóa tổ chức
    def delete(self, request, organization_id):
        if request.user.role not in ['admin', 'organizer']:
            return JsonResponse({"message": "You do not have permission to delete an organization."}, status=403)

        try:
            organization = Organization.objects.get(id=organization_id)
        except Organization.DoesNotExist:
            raise NotFound({"message": "Organization not found"})

        organization.delete()

        return JsonResponse({"message": "Organization deleted successfully"})