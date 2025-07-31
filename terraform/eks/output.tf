output "cluster_id" {
  value = aws_eks_cluster.vkr.id
}

output "node_group_id" {
  value = aws_eks_node_group.vkr.id
}

output "vpc_id" {
  value = aws_vpc.vkr_vpc.id
}

output "subnet_ids" {
  value = aws_subnet.vkr_subnet[*].id
}